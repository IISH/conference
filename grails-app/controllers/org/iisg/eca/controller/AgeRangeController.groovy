package org.iisg.eca.controller

import org.iisg.eca.domain.AgeRange

/**
 * Controller responsible for handling requests on age ranges
 */
class AgeRangeController {
	def pageInformation

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
    }

    /**
     * Shows all data on a particular age range
     */
    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows a list of all age ranges for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new age range for the current event date
     */
    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to the age range
     */
    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

	/**
	 * Removes the age range from the current event date
	 */
	def delete() {
		// Of course we need an id of the age range
		if (params.id) {
			AgeRange ageRange = AgeRange.findByIdAndEvent(params.id, pageInformation.date.event)

			// Try to remove the age range, send back a success or failure message
			if (ageRange?.delete(flush: true)) {
				flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'ageRange.label'), ageRange.toString()])
			}
			else {
				flash.error = true
				flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'ageRange.label'), ageRange.toString()])
			}
		}
		else {
			flash.error = true
			flash.message = g.message(code: 'default.no.id.message')
		}

		redirect(uri: eca.createLink(action: 'list', noBase: true))
	}
}
