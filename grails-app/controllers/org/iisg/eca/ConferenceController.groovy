package org.iisg.eca

/**
 * Controller for conference related actions
 */
class ConferenceController {
    /**
	 * Dependency injection for the springSecurityService
	 */
    def springSecurityService

    /**
     * Default action: redirects to the 'list' action, displaying all conferences
     */
    def index() {
        redirect(action: "list", params: params)
    }

    /**
     * Display all conferences the user has access to and their event dates
     */
    def list() {
        def conferenceInstanceList = User.get(springSecurityService.principal.id).getConferences()
        def dateInstanceList = Date.list(sort:'startDate', order:'desc')
        [conferenceInstanceList: conferenceInstanceList, dateInstanceList: dateInstanceList]
    }
}
