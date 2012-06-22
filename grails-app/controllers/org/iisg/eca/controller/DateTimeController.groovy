package org.iisg.eca.controller

import org.iisg.eca.domain.SessionDateTime

class DateTimeController {
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
    }

    def show() {
        forward(controller: 'dynamicPage', action: 'get', params: params)
    }

    def list() {
        forward(controller: 'dynamicPage', action: 'get', params: params)
    }

    def create() {
        forward(controller: 'dynamicPage', action: 'getAndPost', params: params)
    }

    def edit() {
        forward(controller: 'dynamicPage', action: 'getAndPost', params: params)
    }

    def delete() {
        if (params.id) {
            SessionDateTime sessionDateTime = SessionDateTime.findById(params.id)
            sessionDateTime?.softDelete()

            if (sessionDateTime?.save(flush: true)) {
                flash.message =  message(code: 'default.deleted.message', args: [message(code: 'sessionDateTime.label')])
            }
            else {
                flash.message =  message(code: 'default.not.deleted.message', args: [message(code: 'sessionDateTime.label')])
            }
        }
        else {
            flash.message =  message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}
