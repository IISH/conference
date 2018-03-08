package org.iisg.eca.controller

import org.iisg.eca.domain.ReviewCriteria

/**
 * Controller responsible for handling requests on review criteria
 */
class ReviewCriteriaController {

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
    }

    /**
     * Shows all data on a type of review criteria
     */
    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows a list of all review criteria for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new type of review criteria for the current event date
     */
    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to the type of review criteria
     */
    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Removes the type of review criteria from the current event date
     */
    def delete() {
        // Of course we need an id of the type of review criteria
        if (params.id) {
            ReviewCriteria reviewCriteria = ReviewCriteria.findById(params.id)
            reviewCriteria?.softDelete()

            // Try to remove the type of review criteria, send back a success or failure message
            if (reviewCriteria?.save(flush: true)) {
                flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'reviewCriteria.label'), reviewCriteria.toString()])
            }
            else {
                flash.error = true
                flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'reviewCriteria.label'), reviewCriteria.toString()])
            }
        }
        else {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}
