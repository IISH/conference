package org.iisg.eca.controller

import org.iisg.eca.domain.ElectionsAdvisoryBoard

/**
 * Controller responsible for handling requests on the elections advisory board
 */
class AdvisoryBoardController {

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
    }

    /**
     * Shows a candidate for the elections advisory board
     */
    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows a list of all candidates for the elections advisory board for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new candidate for the advisory board elections of the current event date
     */
    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to the candidate for the advisory board elections
     */
    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Removes the candidate for the advisory board elections from the current event date
     */
    def delete() {
        // Of course we need an id of the type of the electionsAdvisoryBoard
        if (params.id) {
	        ElectionsAdvisoryBoard electionsAdvisoryBoard = ElectionsAdvisoryBoard.findById(params.id)

            // Try to remove the electionsAdvisoryBoard candidate, send back a success or failure message
            if (electionsAdvisoryBoard?.delete(flush: true)) {
                flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'equipment.label'), electionsAdvisoryBoard.toString()])
            }
            else {
                flash.error = true
                flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'equipment.label'), electionsAdvisoryBoard.toString()])
            }
        }
        else {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}
