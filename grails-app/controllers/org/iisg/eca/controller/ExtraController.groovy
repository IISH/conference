package org.iisg.eca.controller

import org.iisg.eca.domain.Extra

/**
 * Controller responsible for handling requests on types of extras
 */
class ExtraController {

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params, flash: flash)
    }

    /**
     * Shows all data on a particular type of extra
     */
    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows a list of all extras for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new extra for the current event date
     */
    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to the extra
     */
    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Removes the extra from the current event date
     */
    def delete() {
        // Of course we need an id of the type of extra
        if (params.id) {
            Extra extra = Extra.findById(params.id)
            extra?.softDelete()

            // Try to remove the type of extra, send back a success or failure message
            if (extra?.save(flush: true)) {
                flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'extra.label')])
            }
            else {
                flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'extra.label')])
            }
        }
        else {
            flash.message = g.message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}
