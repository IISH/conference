package org.iisg.eca.jobs

import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.SentEmail

/**
 * SendEmailJob runs in the background, checking the database for emails waiting to be send and tries to send them
 */
class SendEmailJob {
    /**
     * Service responsible for sending the emails
     */
    def emailService

    /**
     * To check hard coded configuration settings
     */
    def grailsApplication
    
    /**
     * Set the triggers for the time between sending emails
     * Defaults to a minimum of 15 minutes between sending
     */
    static triggers = {
        simple name: "SendEmail", repeatCount: 0
    }
    
    // No concurrent jobs, wait until the previous one is finished
    def concurrent = false
    
    // Group name
    def group = "sendEmailGroup"
    
    /**
     * Check the database for emails waiting to be send and try to send them
     */
    def execute(context) {
        SentEmail.withNewSession { session ->
            Setting emailDisabled = Setting.findByProperty(Setting.DISABLE_EMAIL_SESSIONS)
            boolean hardDisabled = grailsApplication.config.grails.mail.disabled

            log.info("New send email session: time=${new Date()} disabled=${emailDisabled.value.equals('1')} hardDisabled=${hardDisabled}")

            // If not disabled, start sending emails
            if (emailDisabled.value.equals('0') && !hardDisabled) {
                // Find out the maximum number of emails that may be send
                Integer maxMails = new Integer(Setting.findByProperty(Setting.EMAIL_MAX_NUM_EMAILS_PER_SESSION).value)
                // Find out the maximum number of tries
                Integer maxNumTries = new Integer(Setting.findByProperty(Setting.EMAIL_MAX_NUM_TRIES).value)
	            // Find out how many mintues we wait for the email to be sent
	            Integer minutes = new Integer(Setting.findByProperty(Setting.EMAIL_WAITING_TIME).value)
	            minutes = (minutes) ? minutes : 60
	            // Find out the minimum date the email has to be created
	            Date dateTime = geMinimalCreatedTime()

                log.info("Not disabled: querying db with max number of mails = ${maxMails} " +
		                "and max number of tries = ${maxNumTries} and email delay/waiting time = ${minutes} minutes " +
		                "which equals to a minimal creation time of = ${dateTime} unless 'sendAsap' = true")

                // Get all emails that are waiting to be send
                List<SentEmail> emailsWaiting = SentEmail.executeQuery("""
                    FROM SentEmail AS se
                    WHERE se.dateTimeSent IS NULL
                    AND se.numTries < :maxNumTries
                    AND (
                        se.dateTimeCreated <= :dateTime
                        OR se.sendAsap = true
                    )
                """, [maxNumTries: maxNumTries, dateTime: dateTime], [max: maxMails])

                log.info("Found ${emailsWaiting.size()} mails to be send!")

                // Let the email service take care of sending the emails
                for (SentEmail email : emailsWaiting) {
                    emailService.sendEmail(email)
                }
            }

            // Schedule the next email session, in case settings were changed
            Date nextTrigger = getNextTriggerTime()
            schedule(nextTrigger)

            log.info("End of current email session: time=${new Date()} newSessionStarts=${nextTrigger}")
        }
    }
    
    /**
     * Gets the specific timestamp when the next email session should be triggered, defaults to 15 minutes
     * @return The date/time of the next trigger
     */
    Date getNextTriggerTime() {
        Integer minutes = new Integer(Setting.findByProperty(Setting.EMAIL_MIN_MINUTES_BETWEEN_SENDING).value)
        minutes = (minutes) ? minutes : 15

        Calendar cal = Calendar.getInstance()
        cal.setTime(new Date())
        cal.add(Calendar.MINUTE, minutes)

        cal.getTime()
    }

    /**
     * Obtain the minimal created time for emails to be send, defaults to one hour
     * @return The minimal date/time
     */
    Date geMinimalCreatedTime() {
        Integer minutes = new Integer(Setting.findByProperty(Setting.EMAIL_WAITING_TIME).value)
        minutes = (minutes) ? minutes : 60
        minutes = -minutes

        Calendar calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, minutes)

        calendar.getTime()
    }
}
