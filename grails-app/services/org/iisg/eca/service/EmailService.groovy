package org.iisg.eca.service

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.SentEmail
import org.iisg.eca.domain.EventDate
import org.iisg.eca.domain.EmailCode
import org.iisg.eca.domain.EmailTemplate

import org.springframework.mail.MailException

/**
 * Service responsible for all email related actions
 */
class EmailService {
    def mailService
    def pageInformation

    /**
     * Creates an email based on the template and the information about the user
     * @param user The user to which the email has to be send and information has to be embedded in the body text
     * @param emailTemplate The template used for the email content
     * @param date The event date of which to extract participant information from
     * @return The email ready to be send
     */
    SentEmail createEmail(User user, EmailTemplate emailTemplate, EventDate date=pageInformation.date, Map<String, String> additionalValues=[:]) {
        SentEmail email = new SentEmail()
        email.user = user
        email.fromName = emailTemplate.sender
        email.fromEmail = Setting.getByEvent(Setting.findAllByProperty(Setting.DEFAULT_ORGANISATION_EMAIL)).value
        email.subject = emailTemplate.subject
        email.date = date
        email.body = createEmailBody(user, emailTemplate, date, additionalValues)
        email
    }

    /**
     * Tries to send the email, if succeeded the send date will be set in the database
     * @param sentEmail The email to be send
     */
    synchronized void sendEmail(SentEmail sentEmail, boolean saveToDb=true) {
        Integer maxNumTries = new Integer(Setting.getByEvent(Setting.findAllByProperty(Setting.EMAIL_MAX_NUM_TRIES)).value)

        // Only send the email if the maximum number of tries is not reached
        if (sentEmail.numTries < maxNumTries) {
            sentEmail.numTries++

            try {
                mailService.sendMail {
                    from "${sentEmail.fromName} <${sentEmail.fromEmail}>"
                    to "${sentEmail.user.toString()} <${sentEmail.user.email}>"
                    subject sentEmail.subject
                    text sentEmail.body
                }

                // Successfully send, so set the date and time of sending
                sentEmail.dateTimeSent = new Date()
            }
            catch (MailException me) {
                // Make sure, the date/time is set to null, cause it failed to send the email
                sentEmail.dateTimeSent = null
            }

            // Some mails shouldn't be saved in the database
            if (saveToDb) {
                sentEmail.save()
            }
        }
    }

    /**
     * Send an information email to the organizers as configured in the settings
     * @param emailSubject The subject of the email to send
     * @param message The message of the email to send
     */
    synchronized void sendInfoMail(String emailSubject, String message, String emailAddress = null) {
        String[] recipients = Setting.getByEvent(Setting.findAllByProperty(Setting.EMAIL_ADDRESS_INFO_ERRORS)).value.split(';')

        // If no email address is set, use the default info email address from the settings
        if (!emailAddress) {
            emailAddress = "Info email <${Setting.getByEvent(Setting.findAllByProperty(Setting.DEFAULT_ORGANISATION_EMAIL)).value}>"
        }

        mailService.sendMail {
            from emailAddress
            to recipients
            subject emailSubject
            text message
        }
    }

    /**
     * Creates the body for an email message by replacing
     * the codes in the email template with information from the user
     * @param user The user of whom the information has to be included in the email body
     * @param emailTemplate The template to use for the body of an email message
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

        emailBody
    }
}

