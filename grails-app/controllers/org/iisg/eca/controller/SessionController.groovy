package org.iisg.eca.controller

import org.iisg.eca.domain.Session
import org.iisg.eca.domain.User
import org.iisg.eca.domain.ParticipantType

class SessionController {
    def index() {
        redirect(action: 'list', params: params)
    }

    def show() {
        forward(controller: 'dynamicPage', action: 'get')
    }

    def list() {
        forward(controller: 'dynamicPage', action: 'get')
    }

    def create() {
        forward(controller: 'dynamicPage', action: 'getAndPost')
    }

    def edit() {
        Session eventSession = Session.get(params.id)

        if (!eventSession) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'session.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        render(view: "form", model: [eventSession: eventSession, participants: User.list(), types: ParticipantType.list()])
    }
}
