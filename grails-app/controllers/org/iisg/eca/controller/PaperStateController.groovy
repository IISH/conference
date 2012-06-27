package org.iisg.eca.controller

import org.iisg.eca.domain.PaperState

/**
 * Controller responsible for handling requests on paper states
 */
class PaperStateController {

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
    }

    /**
     * Shows all data on a particular paper state
     */
    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows a list of all paper states for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new paper state for the current event date
     */
    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to the paper state
     */
    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Removes the paper state from the current event date
     */
    def delete() {
        // Of course we need an id of the paper state
        if (params.id) {
            PaperState paperState = PaperState.findById(params.id)
            paperState?.softDelete()

            // Try to remove the paper state, send back a success or failure message
            if (paperState?.save(flush: true)) {
                flash.message = g.message(code: 'default.deleted.message', args: [message(code: 'paperState.label')])
            }
            else {
                flash.message = g.message(code: 'default.not.deleted.message', args: [message(code: 'paperState.label')])
            }
        }
        else {
            flash.message = g.message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}
