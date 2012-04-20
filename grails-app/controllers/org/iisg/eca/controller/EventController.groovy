package org.iisg.eca.controller

import org.iisg.eca.domain.EventDate
import org.iisg.eca.domain.User

/**
 * Controller for event related actions
 */
class EventController {
    /**
	 * Dependency injection for the springSecurityService
	 */
    def springSecurityService

    def pageInformation

    /**
     * Redirects to the 'list' action, listing all events if not in event portal
     */
    def index() {
        if (params.event && params.date) {
            return [page: pageInformation.page]
        }
        else {
            redirect(action: "list", params: params)
        }
    }

    /**
     * Switches the user to the event (date) portal based on the event date id
     */
    def switchEvent() {
        if (params.event_switcher) {
            EventDate date = EventDate.get(params.event_switcher)
            if (date) {
                params.event = date.event.url
                params.date = date.url
            }
        }

        redirect(action: "index")
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

        [events: datesByEvent.keySet(), datesByEvent: datesByEvent, allEvents: allEvents, page: pageInformation.page]
    }

    def list_all() {
        forward(controller: 'dynamicPage', action: 'get')
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
