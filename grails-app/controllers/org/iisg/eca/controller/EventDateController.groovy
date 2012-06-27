package org.iisg.eca.controller

/**
 * Controller for event date related actions
 */
class EventDateController {
    /**
     * Holds page information, like the current event date
     */
    def pageInformation

    /**
     * Index action, redirects to the create action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'create', noBase: true), params: params)
    }

    /**
     * Allows the user to create a new event date
     */
    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows all data on an event date
     */
    def show() {
        // Only the current tenant event date
        params.id = pageInformation.date.id

        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to an event date
     */
    def edit() {
        // Only the current tenant event date
        params.id = pageInformation.date.id

        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }
}
