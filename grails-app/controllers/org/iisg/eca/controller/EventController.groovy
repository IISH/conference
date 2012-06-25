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
        if (pageInformation.date) {
            render(view: "index")
        }
        else {
            redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
        }
    }

    /**
     * Switches the user to the event (date) portal based on the event date id
     */
    def switchEvent() {
        String controller = params.controller
        if (params.event_switcher) {
            String[] attrs = params.event_switcher.split("\\|")
            EventDate date = EventDate.get(attrs[0])
            if (date) {
                params.event = date.event.url
                params.date = date.url
            }
            controller = attrs[1]
        }
        params.remove('event_switcher')

        redirect(uri: eca.createLink(controller: controller, action: "index", noBase: true, noPreviousInfo: true))
    }

    /**
     * Display all events the user has access to
     */
    def list() {
        // Get all event dates the user has access to, and sort the dates
        Set<EventDate> dates = User.get(springSecurityService.principal.id).dates
        List<EventDate> datesSorted = new ArrayList<EventDate>()
        EventDate.sortByEventAndDate.list().each { date ->
            if (dates.find { it.id == date.id }) {
                datesSorted.add(date)
            }
        }

        // Loop over all the event dates in search for the events they belong to
        def datesByEvent = [:]  
        datesSorted.each { date ->
            def list = datesByEvent.get(date.event, new ArrayList())
            list.add(date)
        }

        [events: datesByEvent.keySet(), dates: datesByEvent]
    }

    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }
}
