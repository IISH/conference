package org.iisg.eca.controller

import org.iisg.eca.domain.Extra

class ExtraController {
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
        forward(controller: 'dynamicPage', action: 'getAndPost', params: params)
    }

    def edit() {
        forward(controller: 'dynamicPage', action: 'getAndPost', params: params)
    }

    def delete() {
        if (params.id) {
            Extra extra = Extra.findById(params.id)
            extra?.softDelete()

            if (extra?.save(flush: true)) {
                flash.message =  message(code: 'default.deleted.message', args: [message(code: 'extra.label')])
            }
            else {
                flash.message =  message(code: 'default.not.deleted.message', args: [message(code: 'extra.label')])
            }
        }
        else {
            flash.message =  message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'index', noBase: true))
    }
}
