package org.iisg.eca.controller

import org.iisg.eca.domain.Keyword

/**
 * Controller responsible for handling requests on keywords
 */
class KeywordController {
	def pageInformation

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
    }

    /**
     * Shows all data on a particular keyword
     */
    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows a list of all keywords for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new keyword for the current event date
     */
    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to the keyword
     */
    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

	/**
	 * Removes the keyword from the current event date
	 */
	def delete() {
		// Of course we need an id of the keyword
		if (params.id) {
			Keyword keyword = Keyword.findByIdAndEvent(params.id, pageInformation.date.event)

			// Try to remove the keyword, send back a success or failure message
			if (keyword?.delete(flush: true)) {
				flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'keyword.label'), keyword.toString()])
			}
			else {
				flash.error = true
				flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'keyword.label'), keyword.toString()])
			}
		}
		else {
			flash.error = true
			flash.message = g.message(code: 'default.no.id.message')
		}

		redirect(uri: eca.createLink(action: 'list', noBase: true))
	}
}
