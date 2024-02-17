package org.iisg.eca.controller

import org.iisg.eca.domain.PaperType

/**
 * Controller responsible for handling requests on paper types
 */
class PaperTypeController {
	def pageInformation

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
    }

    /**
     * Shows all data on a particular paper type
     */
    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows a list of all paper types for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new paper type for the current event date
     */
    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to the paper type
     */
    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

	/**
	 * Removes the paper type from the current event date
	 */
	def delete() {
		// Of course we need an id of the paper type
		if (params.id) {
			PaperType paperType = PaperType.findByIdAndEvent(params.id, pageInformation.date.event)

			// Try to remove the paper type, send back a success or failure message
			if (paperType?.delete(flush: true)) {
				flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'paperType.label'), paperType.toString()])
			}
			else {
				flash.error = true
				flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'paperType.label'), paperType.toString()])
			}
		}
		else {
			flash.error = true
			flash.message = g.message(code: 'default.no.id.message')
		}

		redirect(uri: eca.createLink(action: 'list', noBase: true))
	}
}
