package org.iisg.eca.controller

import grails.converters.JSON

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.SentEmail
import org.iisg.eca.domain.EmailTemplate

import org.iisg.eca.jobs.CreateEmailJob

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
     * To create a preview email with the currently logged in user
     */
    def springSecurityService

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
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
        Map<String, Boolean> filterMap = emailTemplate.getFilterMap()

        // We also need a email template to be able to send emails
        if (!emailTemplate) {
            flash.error = true
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'email.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // The 'send' button was clicked, create and send the emails
        if (request.post) {
            List<User> users
            def criteria = User.allParticipants(pageInformation.date)
            
            // The email template chosen could contain a query type (a named query to call)
            if (emailTemplate.queryType) {
                criteria = User."${emailTemplate.queryType}"(pageInformation.date)
            }

            // If one specific participant/user is selected, then don't use the other filters
            if (filterMap.participant && params.participant?.isLong()) {
                users = User.findAllById(params.long('participant'))
            }
            else {
                // Now extend the criteria with the filters set by the user
                users = (List<User>) criteria {
                    participantDates {
                        if (filterMap.participantState && params.participantState?.isLong()) {
                            eq('state.id', params.long('participantState'))
                        }

                        if (filterMap.eventDates && (params.eventDates instanceof String) && params.eventDates?.isLong()) {
                            eq('date.id', params.eventDates.toLong())
                        }
                        else if (filterMap.eventDates && params.eventDates) {
                            'in'('date.id', params.eventDates*.toLong())
                        }
                        // If nothing is selected, then nothing is done...
                        else if (filterMap.eventDates && !params.eventDates) {
                            eq('date.id', -100L)
                        }
                    }

                    papers {
                        if (filterMap.paperState && params.paperState?.isLong()) {
                            eq('state.id', params.long('paperState'))
                        }
                    }
                }
            }

            // We have found all the recipients, so we can create all the individual emails
            // But in the background, as it could take a while
            CreateEmailJob.triggerNow([users: users, template: emailTemplate, date: pageInformation.date])

            flash.message = g.message(code: 'email.background.message', args: [users.size()])
        }

        // Find out what auto-complete function to use
        String queryName = 'allParticipants'
        String placeholder = g.message(code: 'email.all.participants.label')

        // TODO: Figure out another way to define these actions; should students be defined here as well?
        if (emailTemplate.queryType == 'networkChairs') {
            queryName = emailTemplate.queryType
            placeholder = g.message(code: 'email.all.network.chairs.label')
        }

        // Create a preview email with the currently logged in user
        User previewUser = User.get(springSecurityService.principal.id)
        SentEmail previewEmail = emailService.createEmail(previewUser, emailTemplate)

        // The labels to and from cause problems because of the characters '<' and '>', so define them here
        String from = "${previewEmail.fromName} <${previewEmail.fromEmail}>".encodeAsHTML()
        String to = "${previewEmail.user.toString()} <${previewEmail.user.email}>".encodeAsHTML()

        // Show the page to select the participant recipients
        render view: "create", model: [
                emailTemplate:  emailTemplate,
                filterMap:      filterMap,
                queryName:      queryName,
                placeholder:    placeholder,
                preview:        previewEmail,
                from:           from,
                to:             to
        ]
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

    /**
     * Creates a new email preview for the currently logged in user
     * (AJAX call)
     */
    def refreshPreview() {
        // Is this really an AJAX call?
        if (request.xhr) {
            User previewUser = User.get(springSecurityService.principal.id)
            EmailTemplate emailTemplate = EmailTemplate.findById(params.id)

            SentEmail previewEmail = emailService.createEmail(previewUser, emailTemplate)
            Map responseMap = [ success:    true,
                                from:       "${previewEmail.fromName} <${previewEmail.fromEmail}>".encodeAsHTML(),
                                to:         "${previewEmail.user.toString()} <${previewEmail.user.email}>".encodeAsHTML(),
                                subject:    previewEmail.subject.encodeAsHTML(),
                                body:       eca.formatText(text: previewEmail.body)]

            render responseMap as JSON
        }
    }
}
