package org.iisg.eca.controller

/**
 * Controller responsible for handling requests on settings
 */
class SettingController {
    def sessionFactory

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
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

    /**
     * Clear all Hibernates caches
     */
    def clearCache() {
        sessionFactory.cache.evictQueryRegions()
        sessionFactory.cache.evictEntityRegions()
        sessionFactory.cache.evictCollectionRegions()
        render "Cache cleared."
    }
}
