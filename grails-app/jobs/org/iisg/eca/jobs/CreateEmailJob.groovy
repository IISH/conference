package org.iisg.eca.jobs

import org.iisg.eca.domain.ParticipantDate

class CreateEmailJob {
    def emailService

    def execute(context) {
        println "Create emails..."

        try {
            for (ParticipantDate participant : context.mergedJobDataMap.get('participants')) {
                emailService.sendEmail(participant.user, context.mergedJobDataMap.get('template'), context.mergedJobDataMap.get('date'))
            }
        }
        catch (Exception e) {
            // TODO: Send to who?
        }
        finally {
            // TODO: Send to who?
        }

        println "Done creating emails..."
    }
}
