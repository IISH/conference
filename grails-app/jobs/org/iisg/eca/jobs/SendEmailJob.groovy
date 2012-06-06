package org.iisg.eca.jobs

import org.quartz.Trigger

import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.SentEmail

class SendEmailJob {
    def emailService
    def quartzScheduler

    static triggers = {
        simple name: 'SendEmail', group: 'sendEmailGroup', startDelay: 0, repeatInterval: 5000
    }

    def concurrent = false

    def execute() {
        println "Start mailing"

        if (Setting.getByEvent(Setting.findAllByProperty(Setting.DISABLE_EMAIL_SESSIONS)).value.equals('0')) {
            println "Not disabled: send mail..."

            Integer maxMails = new Integer(Setting.getByEvent(Setting.findAllByProperty(Setting.EMAIL_MAX_NUM_EMAILS_PER_SESSION)).value)
            List<SentEmail> emailsWaiting = SentEmail.findAllByDateTimeSentIsNull([max: maxMails])
            for (SentEmail email : emailsWaiting) {
                emailService.sendEmail(email)
            }

            rescheduleJob()
        }
        else {
            println "DISABLED! Do not send mail!"
        }

        println "Done mailing"
    }

    def rescheduleJob() {
        println "Reschedule"

        Integer interval = new Integer(Setting.getByEvent(Setting.findAllByProperty(Setting.EMAIL_MIN_MILLISEC_BETWEEN_SENDING)).value)

        if (interval) {
            println "Set trigger: ${interval} ms"

            Trigger trigger = quartzScheduler.getTrigger("SendEmail", "sendEmailGroup")
            trigger.repeatInterval = interval

            quartzScheduler.rescheduleJob(trigger.name, trigger.group, trigger)
        }

        println "Reschedule end"
    }
}
