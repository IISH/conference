package org.iisg.eca.controller

import org.iisg.eca.domain.Title

class TitleController {
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
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
        forward(controller: 'dynamicPage', action: 'getAndPost')
    }

    def delete() {
        if (params.id) {
            Title title = Title.findById(params.id)
            title?.softDelete()

            if (title?.save(flush: true)) {
                flash.message =  message(code: 'default.deleted.message', args: [message(code: 'title.label')])
            }
            else {
                flash.message =  message(code: 'default.not.deleted.message', args: [message(code: 'title.label')])
            }
        }
        else {
            flash.message =  message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}
