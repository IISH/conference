package org.iisg.eca

/**
 * Controller for event related actions
 */
class EventController {
    /**
	 * Dependency injection for the springSecurityService
	 */
    def springSecurityService

    /**
     * Default action: redirects to the 'list' action, listing all events
     */
    def index() {
        redirect(action: "list", params: params)
    }

    /**
     * Display all events the user has access to
     */
    def list() {
        // Get all event dates the user has access to, and sort the dates
        def userDates = User.get(springSecurityService.principal.id).dates
        def allDates = EventDate.sortByEventAndDate.list().grep {
            userDates.contains(it)
        }

        // Loop over all the event dates in search for the events they belong to
        def datesByEvent = [:]
        def allEvents = [:]
        allDates.each { date ->
            def datesForEvent = datesByEvent.get(date.event.shortName, new ArrayList())
            allEvents.put(date.event.shortName, date.event)
            datesForEvent.add(date)
        }

        [events: datesByEvent.keySet(), datesByEvent: datesByEvent, allEvents: allEvents, page: Page.findByControllerAndAction('event', 'list')]
    }

    def show() {
        forward(controller: 'dynamicPage', action: 'get')
    }

    def create() {
        forward(controller: 'dynamicPage', action: 'getAndPost')
    }

    def edit() {
        forward(controller: 'dynamicPage', action: 'getAndPost')
    }
}
