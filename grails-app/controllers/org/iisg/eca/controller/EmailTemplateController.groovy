package org.iisg.eca.controller

import org.iisg.eca.domain.EmailTemplate

class EmailTemplateController {
    def index() {
        redirect(action: 'list', params: params)
    }

    def show() {
        forward(controller: 'dynamicPage', action: 'get', params: params)
    }

    def list() {
        forward(controller: 'dynamicPage', action: 'get', params: params)
    }

    def create() {
        EmailTemplate template = new EmailTemplate()

        if (request.get) {
            render(view: "form", model: [template: template])
        }
        else if (request.post) {
            bindData(template, params, [include: ['usedBy', 'subject', 'body', 'sender', 'comment',
                    'testEmail', 'testAfterSave']], "emailTemplate")

            if (!template.save(flush: true)) {
                render(view: "form", model: [template: template])
                return
            }

            flash.message = message(code: 'default.created.message', args: [message(code: 'emailTemplate.label'), template.id])
            redirect(uri: eca.createLink(action: "show", id: template.id, noBase: true))
        }
    }

    def edit() {
        if (!params.id) {
            flash.message = message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }
        
        EmailTemplate template = EmailTemplate.findById(params.id)

        if (!template) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'emailTemplate.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        if (request.get) {
            render(view: "form", model: [template: template])
        }
        else if (request.post) {
            bindData(template, params, [include: ['usedBy', 'subject', 'body', 'sender', 'comment',
                    'testEmail', 'testAfterSave']], "emailTemplate")

            if (!template.save(flush: true)) {
                render(view: "form", model: [template: template])
                return
            }

            flash.message = message(code: 'default.updated.message', args: [message(code: 'emailTemplate.label'), template.id])
            redirect(uri: eca.createLink(action: "show", id: template.id, noBase: true))
        }
    }
}
