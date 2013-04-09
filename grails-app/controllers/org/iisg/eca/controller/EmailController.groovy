package org.iisg.eca.controller

import org.iisg.eca.jobs.CreateEmailJob

import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.EmailTemplate
import org.iisg.eca.domain.ParticipantDate

/**
 * Controller responsible for handling requests on sending emails
 */
class EmailController {
    /**
     * Holds all page information, like the current event date
     */
    def pageInformation
    
    /**
     * Service responsible for sending the emails
     */
    def emailService

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
    }

    /**
     * Shows a list of all emails that could be send
     */
    def list() {
        // Only those templates that should not be send automatically
        [templates: EmailTemplate.findAllByShowInBackend(true)]
    }

    /**
     * Let's the user select the group of participants to send the emails to and tries to create and send them
     */
    def send() {
        // We need an id, check for the id
        if (!params.id) {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        EmailTemplate emailTemplate = EmailTemplate.findById(params.id)

        // We also need a email template to be able to send emails
        if (!emailTemplate) {
            flash.error = true
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'email.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // The 'send' button was clicked, create and send the emails
        if (request.post) {
            List<ParticipantDate> participants
            def criteria

            // The email template chosen could contain a query type (a named query to call)
            // Otherwise create a new empty criteria object
            // Note: If a participant is chosen, the named query is not called
            if (params.participant?.isLong() || !emailTemplate.queryType) {
                criteria = ParticipantDate.createCriteria()
            }
            else {
                criteria = ParticipantDate."${emailTemplate.queryType}"(pageInformation.date)
            }

            // Now extend the criteria with the filters set by the user
            participants = criteria {
                if (params.state?.isLong()) {
                    eq('state.id', params.long('state'))
                }
                if (params.participant?.isLong()) {
                    eq('id', params.long('participant'))
                }
                if (params.paper?.isLong()) {
                    user {
                        papers {
                            eq('state.id', params.long('paper'))
                        }
                    }
                }
            }

            // We have found all the participants/recipients, so we can create all the individual emails
            // But in the background, as it could take a while
            CreateEmailJob.triggerNow([participants: participants, template: emailTemplate])

            flash.message = g.message(code: 'email.background.message', args: [participants.size()])
        }

        // What type of view to load?
        if (!params.type) {
            params.type = "default"
        }

        // Show the page to select the participant recipients
        // Note: for some reason, ParticipantDate.list() does not work and throws an ArrayIndexOutOfBoundsException
        render view: "filter_${params.type}", model: [emailTemplate: emailTemplate, participants: ParticipantDate.createCriteria().list {}]
    }
    
    /**
     * Allows the user to test the email service
     */
    def test() {
        String statusInfo = null
        params.from = (params.from) ? params.from : Setting.getByEvent(Setting.findAllByProperty(Setting.DEFAULT_ORGANISATION_EMAIL)).value
        
        // The 'send' button was clicked, create and send the emails
        if (request.post) {
            statusInfo = emailService.testEmailService(params.from, params.to, params.subject, params.body)
        }
        
        render view: "test", model: [statusInfo: statusInfo]
    }
}
