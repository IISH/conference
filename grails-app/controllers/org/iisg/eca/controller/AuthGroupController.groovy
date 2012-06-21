package org.iisg.eca.controller

import org.iisg.eca.domain.Group

class AuthGroupController {
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
            Group group = Group.findById(params.id)
            group?.softDelete()

            if (group?.save(flush: true)) {
                flash.message =  message(code: 'default.deleted.message', args: [message(code: 'group.label')])
            }
            else {
                flash.message =  message(code: 'default.not.deleted.message', args: [message(code: 'group.label')])
            }
        }
        else {
            flash.message =  message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'index', noBase: true))
    }
}
