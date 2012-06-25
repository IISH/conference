package org.iisg.eca.controller

import org.iisg.eca.domain.Equipment

class EquipmentController {
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
            Equipment equipment = Equipment.findById(params.id)
            equipment?.softDelete()

            if (equipment?.save(flush: true)) {
                flash.message =  message(code: 'default.deleted.message', args: [message(code: 'equipment.label')])
            }
            else {
                flash.message =  message(code: 'default.not.deleted.message', args: [message(code: 'equipment.label')])
            }
        }
        else {
            flash.message =  message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}
