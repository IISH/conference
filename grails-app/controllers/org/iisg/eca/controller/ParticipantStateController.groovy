package org.iisg.eca.controller

import org.iisg.eca.domain.ParticipantState

class ParticipantStateController {
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
            ParticipantState participantState = ParticipantState.findById(params.id)
            participantState?.softDelete()

            if (participantState?.save(flush: true)) {
                flash.message =  message(code: 'default.deleted.message', args: [message(code: 'participantState.label')])
            }
            else {
                flash.message =  message(code: 'default.not.deleted.message', args: [message(code: 'participantState.label')])
            }
        }
        else {
            flash.message =  message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}
