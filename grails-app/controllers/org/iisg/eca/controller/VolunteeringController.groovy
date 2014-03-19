package org.iisg.eca.controller

import org.iisg.eca.domain.Volunteering

/**
 * Controller responsible for handling requests on volunteering offers
 */
class VolunteeringController {
	def pageInformation

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
    }

    /**
     * Shows all data on a particular volunteering offer
     */
    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows a list of all volunteering offers for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new volunteering offer for the current event date
     */
    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to the volunteering offer
     */
    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Removes the volunteering offer from the current event date
     */
    def delete() {
        // Of course we need an id of the volunteering offer
        if (params.id) {
            Volunteering volunteering = Volunteering.findByIdAndEvent(params.id, pageInformation.date.event)

            // Try to remove the volunteering offer, send back a success or failure message
            if (volunteering?.delete(flush: true)) {
                flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'volunteering.label'), volunteering.toString()])
            }
            else {
                flash.error = true
                flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'volunteering.label'), volunteering.toString()])
            }
        }
        else {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}
