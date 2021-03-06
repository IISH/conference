package org.iisg.eca.controller

import org.iisg.eca.domain.ParticipantState

/**
 * Controller responsible for handling requests on participant states
 */
class ParticipantStateController {
	def pageInformation

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
    }

    /**
     * Shows all data on a particular participant state
     */
    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows a list of all participant states for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new participant state for the current event date
     */
    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to the participant state
     */
    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

	/**
	 * Removes the participant state from the current event date
	 */
	def delete() {
		// Of course we need an id of the participant state
		if (params.id) {
			ParticipantState participantState = ParticipantState.findByIdAndEvent(params.id, pageInformation.date.event)

			// Try to remove the participant state, send back a success or failure message
			if (participantState?.delete(flush: true)) {
				flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'participantState.label'), participantState.toString()])
			}
			else {
				flash.error = true
				flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'participantState.label'), participantState.toString()])
			}
		}
		else {
			flash.error = true
			flash.message = g.message(code: 'default.no.id.message')
		}

		redirect(uri: eca.createLink(action: 'list', noBase: true))
	}
}
