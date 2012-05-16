package org.iisg.eca.controller

import org.iisg.eca.domain.EmailTemplate

class EmailController {
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
        if (request.get) {
            render(view: "form", model: [template: new EmailTemplate()])
        }
        else if (request.post) {
            EmailTemplate template = new EmailTemplate()
            bindData(template, params, [include: ['usedBy', 'subject', 'body', 'sender', 'comment',
                    'testEmail', 'testAfterSave']], "EmailTemplate")

            if (!template.save(flush: true)) {
                render(view: "form", model: [template: template])
                return
            }

           flash.message = message(code: 'default.created.message', args: [message(code: 'emailtemplate.label'), template.id])
           redirect(uri: eca.createLink(action: "show", id: template.id, noBase: true))
        }
    }

    def edit() {
        EmailTemplate template = EmailTemplate.findById(params.id)

        if (!template) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'emailtemplate.label'), template.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        if (request.get) {
            render(view: "form", model: [template: template])
        }
        else if (request.post) {
            bindData(template, params, [include: ['usedBy', 'subject', 'body', 'sender', 'comment',
                    'testEmail', 'testAfterSave']], "EmailTemplate")

            if (!template.save(flush: true)) {
                render(view: "form", model: [template: template])
                return
            }

           flash.message = message(code: 'default.updated.message', args: [message(code: 'emailtemplate.label'), template.id])
            redirect(uri: eca.createLink(action: "show", id: template.id, noBase: true))
        }
    }
}
