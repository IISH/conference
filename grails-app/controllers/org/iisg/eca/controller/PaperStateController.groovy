package org.iisg.eca.controller

import org.iisg.eca.domain.PaperState

class PaperStateController {
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
    }

    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    def delete() {
        if (params.id) {
            PaperState paperState = PaperState.findById(params.id)
            paperState?.softDelete()

            if (paperState?.save(flush: true)) {
                flash.message =  message(code: 'default.deleted.message', args: [message(code: 'paperState.label')])
            }
            else {
                flash.message =  message(code: 'default.not.deleted.message', args: [message(code: 'paperState.label')])
            }
        }
        else {
            flash.message =  message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}
