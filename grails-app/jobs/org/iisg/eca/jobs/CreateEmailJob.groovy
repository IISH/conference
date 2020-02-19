package org.iisg.eca.jobs

import org.iisg.eca.domain.SentEmail
import org.iisg.eca.domain.EventDate
import org.iisg.eca.domain.EmailTemplate

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
        simple repeatCount: 0
    }

    /**
     * Create emails for all users specified, with the template specified,
     * @param context The context containing a map with all users and the template needed
     */
    def execute(context) {
        SentEmail.withNewSession { session ->
            // Load the users to send to, template to use and the event date
            List<Long[]> recipients = context.mergedJobDataMap.get('recipients')
            EmailTemplate template = context.mergedJobDataMap.get('template')
            EventDate date = context.mergedJobDataMap.get('date')
            List<Long> extraParticipantIds = context.mergedJobDataMap.get('extraParticipantIds')

            // If they are correctly send, we can start creating the emails
            if (recipients && template && date) {
                try {
//                    // Successfully created the emails, send a notification email
//                    emailService.sendInfoMail("Started creating the emails for ${recipients.size()} participants", """\
//                        Started creating the emails for ${recipients.size()} participants.
//                        Email template: ${template.id} - ${template.toString()}
//                        The extra participant ids are: ${extraParticipantIds.join(', ')}
//                    """.stripIndent().toString(), date?.event)

                    for (Long[] recipient : recipients) {
                        Map<String, Long> identifiers = template.getIdentifiersMap(recipient)

                        // Let the emailService create all the emails
                        SentEmail email = emailService.createEmail(
                                template, identifiers, true, date, false, [:], extraParticipantIds
                        )
                        email.save()
                    }

                    // Successfully created the emails, send a notification email
                    emailService.sendInfoMail("Succesfully created the emails for ${recipients.size()} participants", """\
                        Succesfully created the emails for ${recipients.size()} participants.
                        Email template: ${template.id} - ${template.toString()}
                        The extra participant ids are: ${extraParticipantIds.join(', ')}
                        The emails will be emailed soon.
                    """.stripIndent().toString(), date?.event)
                }
                catch (Exception e) {
                    StringWriter sw = new StringWriter()
                    e.printStackTrace(new PrintWriter(sw))

                    // Failed to create the emails, send a notification email with the exception thrown
                    emailService.sendInfoMail("Failed to create the emails for ${recipients.size()} participants", """\
                        |Failed to create the emails for ${recipients.size()} participants.
                        |Template used: ${template.description}
                        |Exception: ${sw.toString()}
                    """.stripMargin().toString(), date?.event)
                }
            }
        }
    }
}
