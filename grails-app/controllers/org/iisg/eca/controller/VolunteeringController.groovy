package org.iisg.eca.controller

import org.iisg.eca.domain.Volunteering

class VolunteeringController {
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
            Volunteering volunteering = Volunteering.findById(params.id)
            volunteering?.softDelete()

            if (volunteering?.save(flush: true)) {
                flash.message =  message(code: 'default.deleted.message', args: [message(code: 'volunteering.label')])
            }
            else {
                flash.message =  message(code: 'default.not.deleted.message', args: [message(code: 'volunteering.label')])
            }
        }
        else {
            flash.message =  message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}
