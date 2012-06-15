package org.iisg.eca.jobs

import org.quartz.Trigger

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
     * Scheduler helps resetting the trigger, if necessary
     */
    def quartzScheduler

    /**
     * Set the triggers for the time between sending emails
     * Defaults to a start delay of one minute and a minimum of 15 minutes between sending
     */
    static triggers = {
        simple name: 'SendEmail', group: 'sendEmailGroup', startDelay: 6000, repeatInterval: 1000
    }

    // No concurrent jobs, wait until the previous one is finished
    def concurrent = false

    /**
     * Check the database for emails waiting to be send and try to send them
     */
    def execute() {
        // If not disabled, start sending emails
        if (Setting.getByEvent(Setting.findAllByProperty(Setting.DISABLE_EMAIL_SESSIONS)).value.equals('0')) {
            // Find out the maximum number of emails that may be send
            Integer maxMails = new Integer(Setting.getByEvent(Setting.findAllByProperty(Setting.EMAIL_MAX_NUM_EMAILS_PER_SESSION)).value)
            // Find out the maximum number of tries
            Integer maxNumTries = new Integer(Setting.getByEvent(Setting.findAllByProperty(Setting.EMAIL_MAX_NUM_TRIES)).value)

            // Get all emails that are waiting to be send
            List<SentEmail> emailsWaiting = SentEmail.executeQuery("""
                FROM SentEmail AS se
                WHERE se.dateTimeSent IS NULL
                AND se.numTries < :maxNumTries
            """, [maxNumTries: maxNumTries], [max: maxMails])

            // Let the email service take care of sending the emails
            for (SentEmail email : emailsWaiting) {
                emailService.sendEmail(email)
            }
        }

        // Reschedule the job, in case settings were changed
        rescheduleJob()
    }

    /**
     * Checks the configured repeat interval and reschedules the job
     */
    private void rescheduleJob() {
        // Get the trigger and adjust the repeat interval
        Trigger trigger = quartzScheduler.getTrigger("SendEmail", "sendEmailGroup")
        trigger.repeatInterval = getTimeBetweenSending()

        // And reschedule the job
        quartzScheduler.rescheduleJob(trigger.name, trigger.group, trigger)
    }

    /**
     * Gets the minimum time between sending from the database in minutes and returns it in milliseconds
     * @return The minimum time between sending in milliseconds, defaults to 15 minutes
     */
    Long getTimeBetweenSending() {
        Long interval = new Long(Setting.getByEvent(Setting.findAllByProperty(Setting.EMAIL_MIN_MINUTES_BETWEEN_SENDING)).value)

        if (interval) {
            Long intervalInMs = interval * 6000L
            intervalInMs
        }
        else {
            90000L
        }
    }
}
