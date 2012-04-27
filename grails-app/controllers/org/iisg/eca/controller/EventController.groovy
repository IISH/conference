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
    
    /**
     * Information about the current page
     */
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

        redirect(controller: params.prevController, action: "index", params: params)
    }

    /**
     * Display all events the user has access to
     */
    def list() {
        // Get all event dates the user has access to, and sort the dates
        def dates = User.get(springSecurityService.principal.id).dates
        def datesSorted = new ArrayList()
        EventDate.sortByEventAndDate.list().each { date ->
            if (dates.contains(date)) {
                datesSorted.add(date)
            }
        }

        // Loop over all the event dates in search for the events they belong to
        def datesByEvent = [:]  
        datesSorted.each { date ->
            def list = datesByEvent.get(date.event, new ArrayList())
            list.add(date)
        }

        [events: datesByEvent.keySet(), dates: datesByEvent, page: pageInformation.page]
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
