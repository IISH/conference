package org.iisg.eca.controller

/**
 * Controller responsible for handling requests on settings
 */
class SettingController {

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
    }

    /**
     * Shows a list of all settings for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to the setting
     */
    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }
}
