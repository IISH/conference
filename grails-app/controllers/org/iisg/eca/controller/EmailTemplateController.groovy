package org.iisg.eca.controller

import org.iisg.eca.domain.EmailTemplate

/**
 * Controller responsible for handling requests on email templates
 */
class EmailTemplateController {

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
    }

    /**
     * Shows all data on a particular email template
     */
    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows a list of all email templates for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new email template for the current event date
     */
    def create() {
        EmailTemplate template = new EmailTemplate()

        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all email template related data
            bindData(template, params, [include: ['description', 'subject', 'body', 'sender', 'comment', 'sortOrder',
                    'testEmail', 'testAfterSave']], "emailTemplate")

            // Save the email template and redirect to the previous page if everything is ok
            if (template.save(flush: true)) {
                flash.message = g.message(code: 'default.created.message', args: [g.message(code: 'emailTemplate.label')])
                redirect(uri: eca.createLink(previous: true, noBase: true))
                return
            }
        }

        // Show all email template related information
        render(view: "form", model: [template: template])
    }

    /**
     * Allows the user to make changes to the email template
     */
    def edit() {
        // We need an id, check for the id
        if (!params.id) {
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }
        
        EmailTemplate template = EmailTemplate.findById(params.id)

        // We also need an email template to be able to show something
        if (!template) {
            flash.message = g.message(code: 'default.not.found.message', args: [message(code: 'emailTemplate.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all email template related data
            bindData(template, params, [include: ['description', 'subject', 'body', 'sender', 'comment', 'sortOrder',
                    'testEmail', 'testAfterSave']], "emailTemplate")

            // Save the email template and redirect to the previous page if everything is ok
            if (template.save(flush: true)) {
                flash.message = g.message(code: 'default.updated.message', args: [g.message(code: 'emailTemplate.label')])
                redirect(uri: eca.createLink(action: "show", id: template.id, noBase: true))
                return
            }
        }

        // Show all email template related information
        render(view: "form", model: [template: template])
    }

    /**
     * Removes the email template from the current event date
     */
    def delete() {
        // Of course we need an id of the email template
        if (params.id) {
            EmailTemplate emailTemplate = EmailTemplate.findById(params.id)
            emailTemplate?.softDelete()

            // Try to remove the email template, send back a success or failure message
            if (emailTemplate?.save(flush: true)) {
                flash.message = g.message(code: 'default.deleted.message', args: [message(code: 'emailTemplate.label')])
            }
            else {
                flash.message = g.message(code: 'default.not.deleted.message', args: [message(code: 'emailTemplate.label')])
            }
        }
        else {
            flash.message = g.message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}