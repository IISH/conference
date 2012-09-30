package org.iisg.eca.controller

import org.iisg.eca.domain.SessionDateTime

/**
 * Controller responsible for handling requests on session date/times
 */
class DateTimeController {

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
    }

    /**
     * Shows all data on a particular date/time
     */
    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows a list of all date/times for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new date/time for the current event date
     */
    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to the date/time
     */
    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Removes the date/time from the current event date
     */
    def delete() {
        // Of course we need an id of the session date/time
        if (params.id) {
            SessionDateTime sessionDateTime = SessionDateTime.findById(params.id)
            sessionDateTime?.softDelete()

            // Try to remove the date/time, send back a success or failure message
            if (sessionDateTime?.save(flush: true)) {
                flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'sessionDateTime.label'), sessionDateTime.toString()])
            }
            else {
                flash.error = true
                flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'sessionDateTime.label'), sessionDateTime.toString()])
            }
        }
        else {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}
