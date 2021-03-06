package org.iisg.eca.controller

import org.iisg.eca.domain.ParticipantType

/**
 * Controller responsible for handling requests on participant types
 */
class ParticipantTypeController {
	def pageInformation

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
    }

    /**
     * Shows all data on a particular participant type
     */
    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows a list of all participant types for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new participant type for the current event date
     */
    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to the participant type
     */
    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

	/**
	 * Removes the participant type from the current event date
	 */
	def delete() {
		// Of course we need an id of the email code
		if (params.id) {
			ParticipantType participantType = ParticipantType.findByIdAndEvent(params.id, pageInformation.date.event)

			// Try to remove the participant type, send back a success or failure message
			if (participantType?.delete(flush: true)) {
				flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'participantType.label'), participantType.toString()])
			}
			else {
				flash.error = true
				flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'participantType.label'), participantType.toString()])
			}
		}
		else {
			flash.error = true
			flash.message = g.message(code: 'default.no.id.message')
		}

		redirect(uri: eca.createLink(action: 'list', noBase: true))
	}
}
