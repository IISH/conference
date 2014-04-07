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
     * Service responsible for obtaining the email recipients
     */
    def emailRecipientsService

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
            // Find all the recipients, so we can create all the individual emails
            List<Long[]> recipients = emailRecipientsService.getRecipientsFor(emailTemplate, params)

            // But place the actual creation in the background, as it could take a while
            CreateEmailJob.triggerNow([recipients: recipients, template: emailTemplate, date: pageInformation.date])

            flash.message = g.message(code: 'email.background.message', args: [recipients.size()])
        }

        // Find out what auto-complete function to use
        String queryName = 'allParticipants'
        String placeholder = g.message(code: 'email.all.participants.label')
        if (emailTemplate.queryTypeOne) {
	        queryName = emailTemplate.queryTypeOne
        }

        // Create a preview email with the currently logged in user
        User previewUser = User.get(springSecurityService.principal.id)
        SentEmail previewEmail = emailService.createEmail(previewUser, emailTemplate, false)

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
        params.from = (params.from) ? params.from : Setting.getSetting(Setting.DEFAULT_ORGANISATION_EMAIL).value
        
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

            SentEmail previewEmail = emailService.createEmail(previewUser, emailTemplate, false)
            Map responseMap = [ success:    true,
                                from:       "${previewEmail.fromName} <${previewEmail.fromEmail}>".encodeAsHTML(),
                                to:         "${previewEmail.user.toString()} <${previewEmail.user.email}>".encodeAsHTML(),
                                subject:    previewEmail.subject.encodeAsHTML(),
                                body:       eca.formatText(text: previewEmail.body)]

            render responseMap as JSON
        }
    }
}
