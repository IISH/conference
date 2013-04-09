package org.iisg.eca.service

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.SentEmail
import org.iisg.eca.domain.EventDate
import org.iisg.eca.domain.EmailCode
import org.iisg.eca.domain.EmailTemplate
import org.iisg.eca.domain.ParticipantDate

import javax.mail.internet.MimeMessage

import org.springframework.mail.MailMessage
import org.springframework.mail.MailException

import org.springframework.mail.javamail.MimeMailMessage

/**
 * Service responsible for all email related actions
 */
class EmailService {
    /**
     * Grails application service to access configuration values
     */
    def grailsApplication
    
    /**
     * The standard mail service
     */
    def mailService

    /**
     * Includes information about the page, like the event date
     */
    def pageInformation

    /**
     * Creates an email based on the template and the information about the user
     * @param user The user to which the email has to be send and information has to be embedded in the body text
     * @param emailTemplate The template used for the email content
     * @param date The event date of which to extract participant information from
     * @param additionalValues A map of additional values to add to the email
     * @return The email ready to be send
     */
    SentEmail createEmail(User user, EmailTemplate emailTemplate, EventDate date=pageInformation.date, Map<String, String> additionalValues=[:]) {
        SentEmail email = new SentEmail()

        // Set all the email properties
        email.user = user
        email.fromName = emailTemplate.sender
        email.fromEmail = Setting.getByEvent(Setting.findAllByProperty(Setting.DEFAULT_ORGANISATION_EMAIL)).value
        email.subject = emailTemplate.subject
        email.date = date
        email.body = createEmailBody(user, emailTemplate, date, additionalValues)
        //email.queryType = emailTemplate.queryType

        email
    }

    /**
     * Tries to send the email, if succeeded the send date will be set in the database
     * @param sentEmail The email to be send
     * @param saveToDb Whether the email should be saved in the database
     */
    synchronized void sendEmail(SentEmail sentEmail, boolean saveToDb=true) {
        // How often may we try before giving up?
        Integer maxNumTries = new Integer(Setting.getByEvent(Setting.findAllByProperty(Setting.EMAIL_MAX_NUM_TRIES)).value)

        // Only send the email if the maximum number of tries is not reached
        if (sentEmail.numTries < maxNumTries) {
            sentEmail.numTries++

            try {
                // Try to send the email
                mailService.sendMail {
                    from "${sentEmail.fromName} <${sentEmail.fromEmail}>"
                    to "${sentEmail.user.toString()} <${sentEmail.user.email}>"
                    subject sentEmail.subject
                    text sentEmail.body
                }

                // Successfully send, so set the date and time of sending
                sentEmail.dateTimeSent = new Date()
                
                // TODO: Update the participant
                /*ParticipantDate participant = ParticipantDate.findByUserAndDate(email.user, email.date)
                participant.updateByQueryType(email.queryType)   
                participant.save()  */
            }
            catch (MailException me) {
                // Make sure, the date/time is set to null, cause it failed to send the email
                sentEmail.dateTimeSent = null
            }
            finally {
                // Some mails shouldn't be saved in the database
                if (saveToDb) {
                    SentEmail.withNewSession { session ->
                        sentEmail.internalUpdate = true
                        sentEmail.save()
                    }
                }
            }
        }
    }

    /**
     * Send an information email to the organizers as configured in the settings
     * @param emailSubject The subject of the email to send
     * @param message The message of the email to send
     * @param emailAddress Specify from which email address the email originated
     */
    synchronized void sendInfoMail(String emailSubject, String message, String emailAddress = null) {
        String[] recipients = Setting.getByEvent(Setting.findAllByProperty(Setting.EMAIL_ADDRESS_INFO_ERRORS)).value.split(';')

        // If no email address is set, use the default info email address from the settings
        if (!emailAddress) {
            emailAddress = "Info email <${Setting.getByEvent(Setting.findAllByProperty(Setting.DEFAULT_ORGANISATION_EMAIL)).value}>"
        }

        // Send the email
        mailService.sendMail {
            from emailAddress
            to recipients
            subject emailSubject
            text message
        }
    }
    
    /**
     * Send a test email and return relevant information about the email
     * @param f From email address
     * @param t To email address
     * @param s Subject of the email
     * @param body Body of the email
     * @return Relevant information about the email
     */
    synchronized String testEmailService(String f, String t, String s, String body) {
        String info = "";       
        
        try {
            // Make sure that the mail service is enabled.
            if (grailsApplication.config.grails.mail.disabled) {
                throw new Exception("The mail service is disabled. " +
                    "Please change the value of 'grails.mail.disabled' " +
                    "in Config.groovy.");
            }
            
            // Make sure that the mail service is also enabled in the database.
            if (Setting.getByEvent(Setting.findAllByProperty(Setting.DISABLE_EMAIL_SESSIONS)).value.equals('1')) {
                throw new Exception("The mail service is disabled. " +
                    "Please change the value of '${Setting.DISABLE_EMAIL_SESSIONS}' " +
                    "in the Settings table of the database.");
            }
            
            // Send the email
            MimeMailMessage mailMessage = mailService.sendMail {
                from f
                to t
                subject s
                text body
            }
            
            // Get the info from the email message
            MimeMessage message = mailMessage.getMimeMessage()
            
            // Get header information
            for (String header : message.getAllHeaderLines()) {
                info += "${header} \n"
            }
            info += "\n"
            
            // Get content information
            info += "Content: \n${message.getContent()} \n\n"            
        }
        catch (Exception e) {
            StringWriter sw = new StringWriter()
            e.printStackTrace(new PrintWriter(sw))
            info += "Exception: ${sw.toString()} \n"
        }
        finally {
            return info.trim()
        }
    }

    /**
     * Creates the body for an email message by replacing
     * the codes in the email template with information from the user
     * @param user The user of whom the information has to be included in the email body
     * @param emailTemplate The template to use for the body of an email message
     * @param date The event date of which to extract participant information from
     * @param additionalValues A map of additional values to add to the email
     * @return The body text from the template combined with information from the user
     */
    String createEmailBody(User user, EmailTemplate emailTemplate, EventDate date=pageInformation.date, Map<String, String> additionalValues=[:]) {
        String emailBody = emailTemplate.body.toString()
        
        // Collect all available codes and check for each one whether the email uses the code
        EmailCode.list().each { code ->
            if (emailBody.contains("[${code.code}]")) {
                // If the email contains the code, replace all occurrences with user specific information
                emailBody = emailBody.replaceAll("\\[${code.code}\\]", code.translateForUser(user, date))
            }
        }
        
        // Do the same for additional values
        additionalValues.each { additionalValue ->
            if (emailBody.contains("[${additionalValue.key}]")) {
                // If the email contains the code, replace all occurrences with the value
                emailBody = emailBody.replaceAll("\\[${additionalValue.key}\\]", additionalValue.value)
            }
        }
        
        // Also update the special code SenderName
        if (emailBody.contains("[SenderName]")) {
            // If the email contains the code, replace all occurrences with the value
            emailBody = emailBody.replaceAll("\\[SenderName\\]", emailTemplate.sender?.trim())
        }
        
        emailBody
    }
}

