package org.iisg.eca.service

import org.iisg.eca.domain.User
import org.iisg.eca.domain.EmailCode
import org.iisg.eca.domain.EmailTemplate

import org.springframework.mail.MailMessage
import org.springframework.mail.MailException
import org.iisg.eca.domain.SentEmail
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.EventDate

/**
 * Service responsible for all email related actions
 */
class EmailService {
    def mailService
    def pageInformation

    /**
     * Creates the email based on the template and sends it to the database, waiting to be send
     * @param user The user to which the email has to be send
     * @param emailTemplate The template used for the email content
     */
    synchronized void sendEmail(User user, EmailTemplate emailTemplate, boolean saveInDb=true, boolean sendImmediately=false) {
        SentEmail email = new SentEmail()
        email.user = user
        email.from = emailTemplate.sender
        email.fromEmail = 'default'
        email.subject = emailTemplate.subject
        email.body = createEmailBody(user, emailTemplate)

        if (saveInDb) {
            email.save()
        }
        if (sendImmediately) {
            sendEmail(email)
        }
    }

    /**
     *
     * @param sentEmail
     */
    synchronized void sendEmail(SentEmail sentEmail) {
        Integer maxNumTries = new Integer(Setting.getByEvent(Setting.findAllByProperty(Setting.EMAIL_MAX_NUM_TRIES)).value)

        if (sentEmail.numTries < maxNumTries) {
            sentEmail.numTries++

            try {
                MailMessage message = mailService.sendMail {
                    to "${sentEmail.user.toString()} <${sentEmail.user.email}>"
                    subject sentEmail.subject
                    text sentEmail.body
                }
                sentEmail.dateTimeSent = new Date()
            }
            finally {
                sentEmail.save()
            }
        }
    }

    /**
     * Creates the body for an email message by replacing
     * the codes in the email template with information from the user
     * @param user The user of whom the information has to be included in the email body
     * @param emailTemplate The template to use for the body of an email message
     * @return The body text from the template combined with information from the user
     */
    String createEmailBody(User user, EmailTemplate emailTemplate, EventDate date = pageInformation.date) {
        String emailBody = emailTemplate.body.toString()

        // Collect all available codes and check for each one whether the email uses the code
        EmailCode.list().each { code ->
            if (emailBody.contains("[${code.code}]")) {
                // If the email contains the code, replace all occurrences with user specific information
                emailBody = emailBody.replaceAll("\\[${code.code}\\]", code.translateForUser(user, date))
            }
        }

        emailBody
    }
}

