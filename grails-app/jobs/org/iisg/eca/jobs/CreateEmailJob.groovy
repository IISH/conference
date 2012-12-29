package org.iisg.eca.jobs

import org.iisg.eca.domain.SentEmail
import org.iisg.eca.domain.EmailTemplate
import org.iisg.eca.domain.ParticipantDate

/**
 * CreateEmailJob is responsible for the creation of a large amount of emails ready to be send in a separate thread
 */
class CreateEmailJob {
    /**
     * The email service responsible for the creation of the emails
     */
    def emailService

    /**
     * Make sure the job is only executed once, only when explicitly triggered
     */
    static triggers = {
        simple name: 'CreateEmail', group: 'createEmailGroup', repeatInterval: 1000, repeatCount: 0
    }

    /**
     * Create emails for all participants specified, with the template specified,
     * @param context The context containing a map with all participants and the template needed
     */
    def execute(context) {
        // Load the participants to send to and template to use
        List<ParticipantDate> participants = context.mergedJobDataMap.get('participants')
        EmailTemplate template = context.mergedJobDataMap.get('template')

        // If they are correctly send, we can start creating the emails
        if (participants && template) {
            try {
                for (ParticipantDate participant : participants) {
                    // Let the emailService create all the emails
                    SentEmail email = emailService.createEmail(participant.user, template, participant.date)
                    email.save()
                }

                // Successfully created the emails, send a notification email
                emailService.sendInfoMail("Succesfully created the emails for ${participants.size()} participants", """
                    Succesfully created the emails for ${participants.size()} participants.
                    The emails will be emailed soon.
                """)
            }
            catch (Exception e) {
                StringWriter sw = new StringWriter()
                e.printStackTrace(new PrintWriter(sw))

                // Failed to create the emails, send a notification email with the exception thrown
                emailService.sendInfoMail("Failed to create the emails for ${participants.size()} participants", """
                    Failed to create the emails for ${participants.size()} participants.
                    Template used: ${template.description}
                    Exception: ${sw.toString()}
                """)
            }
        }
    }
}
