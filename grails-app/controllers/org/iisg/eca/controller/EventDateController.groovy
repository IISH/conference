package org.iisg.eca.controller

import org.iisg.eca.domain.Event
import org.iisg.eca.domain.User
import org.iisg.eca.domain.EventDate
import org.iisg.eca.domain.Day

/**
 * Controller for event date related actions
 */
class EventDateController {
    /**
     * Holds page information, like the current event date
     */
    def pageInformation

    /**
     * Service responsible for information about the currently logged in user
     */
    def springSecurityService

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
        // We need an id of the event to add it to, check for the id
        if (!params.id) {
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        Event event = Event.get(params.id)
        User user = User.get(springSecurityService.principal.id)

        // Make sure we have an event and user, and the user is allowed access to that event
        if (event && user && user.dates.find { it.event.id == event.id }) {
            EventDate eventDate = new EventDate(event: event)

            // The 'save' button was clicked, save all data
            if (request.post) {
                // Save all event date related data
                bindData(eventDate, params, [include: ["yearCode", "startDate", "endDate", "dateAsText",
                        "description", "longDescription"]], "EventDate")

                // Save all the days
                int i = 0
                while (params["Day_${i}"]) {
                    Day day = new Day()
                    bindData(day, params, [include: ['dayNumber', 'day']], "Day_${i}")
                    eventDate.addToDays(day)
                    i++
                }

                // Save the event date and redirect to the previous page if everything is ok
                if (eventDate.save()) {
                    flash.message = g.message(code: 'default.created.message', args: [g.message(code: 'eventDate.label')])
                    redirect(uri: eca.createLink(previous: true, noBase: true))
                    return
                }
            }

            // Show all event date related information
            render(view: "form", model: [eventDate: eventDate])
        }
        else {
            // Sorry, but the user is not allowed to do this
            response.sendError(403)
        }
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
