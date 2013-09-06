package org.iisg.eca.controller

import org.iisg.eca.domain.User
import org.iisg.eca.domain.EventDate
import org.iisg.eca.domain.StatisticsTemplate
import org.iisg.eca.domain.Statistic
import org.iisg.eca.domain.Event

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
            StatisticsTemplate statisticsTemplate = StatisticsTemplate.getByEvent(StatisticsTemplate.list())
            String statistics = statisticsTemplate?.template

            if (statistics) {
                // Collect all available statistics and check for each one whether the template uses the property
                Statistic.list().each { statistic ->
                    if (statistics.contains("[${statistic.property}]")) {
                        // If the template contains the property, replace all occurrences with the specific value
                        statistics = statistics.replaceAll("\\[${statistic.property}\\]", statistic.value)
                    }
                }

                // Any properties not found are replaced by 0
                statistics = statistics.replaceAll("\\[[^\\[\\]]+\\]", "0")
            }

            render(view: "index", model: [statistics: statistics])
        }
        else {
            redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
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

        redirect(uri: eca.createLink(controller: controller, action: "index", noBase: true, noPreviousInfo: true, params: params))
    }

    /**
     * Display all events the user has access to
     */
    def list() {
        // Get all event dates the user has access to, and sort the dates
        List<Event> events = User.get(springSecurityService.principal.id).events

        // Loop over all the events and collect the event dates
        Map datesByEvent = [:]
        events.each { event ->
            datesByEvent.put(event, EventDate.getAllForEvent(event).list())
        }

        [events: events, dates: datesByEvent]
    }

    /**
     * Allows the user to create a new event
     */
    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows all data on an event
     */
    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to an event
     */
    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }
}
