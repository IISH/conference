package org.iisg.eca.jobs

import org.quartz.Trigger
import static org.quartz.TriggerBuilder.*
import static org.quartz.SimpleScheduleBuilder.*

import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.SentEmail
import org.iisg.eca.domain.ParticipantDate

/**
 * SendEmailJob runs in the background, checking the database for emails waiting to be send and tries to send them
 */
class SendEmailJob {
    /**
     * Service responsible for sending the emails
     */
    def emailService

    def grailsApplication
    
    /**
     * Set the triggers for the time between sending emails
     * Defaults to a minimum of 15 minutes between sending
     */
    static triggers = {
        simple name: "SendEmail", repeatInterval: 900000, repeatCount: 0
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

            log.warn("New send email session: time=${new Date()} disabled=${emailDisabled.value.equals('1')} hardDisabled=${hardDisabled}")

            // If not disabled, start sending emails
            if (emailDisabled.value.equals('0') && !hardDisabled) {
                // Find out the maximum number of emails that may be send
                Integer maxMails = new Integer(Setting.findByProperty(Setting.EMAIL_MAX_NUM_EMAILS_PER_SESSION).value)
                // Find out the maximum number of tries
                Integer maxNumTries = new Integer(Setting.findByProperty(Setting.EMAIL_MAX_NUM_TRIES).value)

                log.warn("Not disabled: querying db with maxMails=${maxMails} and maxNumTries=${maxNumTries}")

                // Get all emails that are waiting to be send
                List<SentEmail> emailsWaiting = SentEmail.executeQuery("""
                FROM SentEmail AS se
                WHERE se.dateTimeSent IS NULL
                AND se.numTries < :maxNumTries
            """, [maxNumTries: maxNumTries], [max: maxMails])

                log.warn("Found ${emailsWaiting.size()} mails to be send!")

                // Let the email service take care of sending the emails
                for (SentEmail email : emailsWaiting) {
                    emailService.sendEmail(email)
                }
            }

            // Reschedule the job, in case settings were changed
            int minutes = getMinutesBetweenSending()
            Trigger newTrigger = getTrigger(minutes)
            reschedule(newTrigger)

            Calendar cal = Calendar.getInstance()
            cal.setTime(new Date())
            cal.add(Calendar.MINUTE, minutes)
            log.warn("End of current email session: time=${new Date()} minutesBetweenSending=${minutes} newSessionStarts=${cal.getTime()}")
        }
    }
    
    /**
     * Gets the minimum time between sending from the database in minutes
     * @return The minimum time between sending in seconds, defaults to 15 minutes
     */
    int getMinutesBetweenSending() {
        Integer interval = new Integer(Setting.findByProperty(Setting.EMAIL_MIN_MINUTES_BETWEEN_SENDING).value)
        (interval) ? interval : 15
    }
    
    /**
     * Create a new trigger for the time between sending emails
     */
    static Trigger getTrigger(int minutes) {
        // Compute the new trigger time
        Calendar cal = Calendar.getInstance()
        cal.setTime(new Date())
        cal.add(Calendar.MINUTE, minutes)

        // Create the new trigger
        newTrigger()
            .withIdentity("SendEmail")
            .withSchedule(
                simpleSchedule()
                    .withRepeatCount(0)
            )
            .startAt(cal.getTime())
            .build()
    }
}
