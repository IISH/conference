package org.iisg.eca.service

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Event
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.SentEmail
import org.iisg.eca.domain.EventDate
import org.iisg.eca.domain.EmailCode
import org.iisg.eca.domain.EmailTemplate

import javax.mail.internet.MimeMessage

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
     * @param updateRecords Indicates whether related records of the recipient should be updated
     * @param identifiers A map with ids that should identify the type of information the body should contain
     * @param date The event date of which to extract participant information from
     * @param sendAsap Whether the email should be send as soon as possible
     * @param additionalValues A map of additional values to add to the email
     * @return The email ready to be send
     */
    SentEmail createEmail(User user, EmailTemplate emailTemplate, boolean updateRecords=true,
                          Map<String, Long> identifiers=[:], EventDate date=pageInformation.date,
                          boolean sendAsap=false, Map<String, String> additionalValues=[:]) {
        // First make sure the identifiers are correctly set
        if (!identifiers) {
            identifiers = [:]
        }
        if (!identifiers.containsKey(EmailTemplate.USER_ID)) {
            identifiers[EmailTemplate.USER_ID] = user.id
        }
        if (!identifiers.containsKey(EmailTemplate.DATE_ID) && date) {
            identifiers[EmailTemplate.DATE_ID] = date.id
        }

        // Set all the email properties
        SentEmail email = new SentEmail()

        email.user = user
        email.fromName = emailTemplate.sender
        email.fromEmail = Setting.getSetting(Setting.DEFAULT_ORGANISATION_EMAIL, date?.event).value
        email.subject = emailTemplate.subject
	    email.body = emailTemplate.body
        email.date = date
        email.sendAsap = sendAsap

	    // Translate the codes in the email
	    translateCodes(emailTemplate, email, identifiers, additionalValues)

        // Update the recipients records, as his email is created and ready to be send
        if (updateRecords) {
            emailTemplate.updateRecipient(identifiers)
        }

        email
    }

    /**
     * Creates an email based on the template and the information about the user
     * @param emailTemplate The template used for the email content     *
     * @param identifiers A map with ids that should identify the type of information the body should contain
     * @param updateRecords Indicates whether related records of the recipient should be updated
     * @param date The event date of which to extract participant information from
     * @param sendAsap Whether the email should be send as soon as possible
     * @param additionalValues A map of additional values to add to the email
     * @return The email ready to be send
     */
    SentEmail createEmail(EmailTemplate emailTemplate, Map<String, Long> identifiers, boolean updateRecords=true,
                          EventDate date=pageInformation.date, boolean sendAsap=false,
                          Map<String, String> additionalValues=[:]) {
        User user = User.get(identifiers.get(EmailTemplate.USER_ID))
        createEmail(user, emailTemplate, updateRecords, identifiers, date, sendAsap, additionalValues)
    }

    /**
     * Tries to send the email, if succeeded the send date will be set in the database
     * @param sentEmail The email to be send
     * @param saveToDb Whether the email should be saved in the database
     * @param forceSend Whether the email is forced to (re)send
     */
    synchronized void sendEmail(SentEmail sentEmail, boolean saveToDb=true, boolean forceSend=false) {
        // How often may we try before giving up?
        Integer maxNumTries = new Integer(Setting.getSetting(Setting.EMAIL_MAX_NUM_TRIES).value)

        // Only send the email if the maximum number of tries is not reached
        if (forceSend || (sentEmail.numTries < maxNumTries)) {
            if (!sentEmail.dateTimeSent) {
	            sentEmail.numTries++
            }

            try {
	            String fromEmail = Setting.getSetting(Setting.DEFAULT_FROM_EMAIL).value
	            String[] bccAddresses = Setting.getSetting(Setting.EMAIL_BCC).getMultipleValues()

                // Try to send the email if we have to
                if (sendEmailTo(sentEmail.user, sentEmail.date?.event)) {
                    mailService.sendMail {
                        from "\"${sentEmail.fromName}\" <${fromEmail}>"
	                    replyTo sentEmail.fromEmail
	                    bcc bccAddresses
	                    to "\"${sentEmail.user.toString()}\" <${sentEmail.user.email}>"
                        subject sentEmail.subject
                        text sentEmail.body
                    }
                }

                // Successfully send, so set the date and time of sending
                sentEmail.updateDateTimeSent()
            }
            catch (MailException me) {
                // Make sure, the date/time is set to null, cause it failed to send the email
                sentEmail.dateTimeSent = null

                log.error("Failed to sent mail: $me.message")
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
     * @param event From which event is the email originating
     * @param emailAddress Specify from which email address the email originated
     */
    synchronized void sendInfoMail(String emailSubject, String message,
                                   Event event=pageInformation.date?.event, String emailAddress=null) {
        String[] recipients = Setting.getSetting(Setting.EMAIL_ADDRESS_INFO_ERRORS, event).getMultipleValues()
	    String fromEmail = Setting.getSetting(Setting.DEFAULT_FROM_EMAIL).value

        // If no email address is set, use the default info email address from the settings
        if (!emailAddress) {
            emailAddress = "Info email <${Setting.getSetting(Setting.DEFAULT_ORGANISATION_EMAIL, event).value}>"
        }

        // Send the email
        mailService.sendMail {
	        from fromEmail
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
	    String fromEmail = Setting.getSetting(Setting.DEFAULT_FROM_EMAIL).value
	    String[] bccAddresses = Setting.getSetting(Setting.EMAIL_BCC).getMultipleValues()

        try {
            // Make sure that the mail service is enabled.
            if (grailsApplication.config.grails.mail.disabled) {
                throw new Exception("The mail service is disabled. " +
                    "Please change the value of 'grails.mail.disabled' " +
                    "in Config.groovy.");
            }

            // Make sure that the mail service is also enabled in the database.
            if (Setting.getSetting(Setting.DISABLE_EMAIL_SESSIONS).value.equals('1')) {
                throw new Exception("The mail service is disabled. " +
                    "Please change the value of '${Setting.DISABLE_EMAIL_SESSIONS}' " +
                    "in the Settings table of the database.");
            }

            // Send the email
            MimeMailMessage mailMessage = (MimeMailMessage) mailService.sendMail {
	            from fromEmail
                replyTo f
	            bcc bccAddresses
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
	 * Translates the codes in the body/subject for an email message by replacing
	 * the codes in the email with information from the user
	 * @param template The email template used
	 * @param email The email message
	 * @param identifiers The ids of records to extract participant information from
	 * @param additionalValues A map of additional values to add to the email
	 */
    void translateCodes(EmailTemplate template, SentEmail email, Map<String, Long> identifiers, Map<String, String> additionalValues=[:]) {
        // Collect all available codes and check for each one whether the email uses the code
        EmailCode.list().each { code ->
	        // If the email contains the code, replace all occurrences with user specific information
            if (email.body.contains("[${code.code}]")) {
	            email.body = email.body.replace("[$code.code]", code.translate(identifiers))
            }

	        if (email.subject.contains("[${code.code}]")) {
		        email.subject = email.subject.replace("[$code.code]", code.translate(identifiers))
	        }
        }

        // Do the same for additional values
        additionalValues.each { additionalValue ->
            if (email.body.contains("[${additionalValue.key}]")) {
	            email.body = email.body.replace("[$additionalValue.key]", additionalValue.value)
            }

	        if (email.subject.contains("[${additionalValue.key}]")) {
		        email.subject = email.subject.replace("[$additionalValue.key]", additionalValue.value)
	        }
        }

        // Also update the special code SenderName
        if (email.body.contains("[SenderName]")) {
	        email.body = email.body.replace("[SenderName]", template.sender?.trim())
        }

	    if (email.subject.contains("[SenderName]")) {
		    email.subject = email.subject.replace("[SenderName]", template.sender?.trim())
	    }
    }

    /**
     * Find out whether we have to send the emails really to this user
     * @param user The user to check
     * @param event The event for which the email is
     * @return Whether we have to send mails to this address
     */
    private boolean sendEmailTo(User user, Event event) {
        Setting regexSettingsForEvent = Setting.getSetting(Setting.DONT_SEND_EMAILS_TO, event)
        String[] regexs = regexSettingsForEvent.value?.split()

        if (regexs && regexs.length > 0) {
            for (String regex : regexs) {
                if (user.email.matches(regex)) {
                    return false
                }
            }
        }

        return !user.emailDiscontinued
    }
}
