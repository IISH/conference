package org.iisg.eca.controller

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.User
import org.iisg.eca.domain.Event
import org.iisg.eca.domain.EventDate

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
        if (pageInformation.date) {
            redirect(uri: eca.createLink(action: 'show', noBase: true, noPreviousInfo: true, params: params))
        }
        else if (params.id) {
            redirect(uri: eca.createLink(action: 'create', noBase: true, noPreviousInfo: true, params: params))
        }
        else {
            redirect(uri: eca.createLink(controller: 'event', action: 'index', noBase: true, noPreviousInfo: true, params: params))
        }
    }

    /**
     * Allows the user to create a new event date
     */
    def create() {
        // We need an id of the event to add it to, check for the id
        User user = User.get(springSecurityService.principal.id)

        // Make sure we have an user
        if (pageInformation.date.event && user) {
            EventDate eventDate = new EventDate(event: pageInformation.date.event)

            // The 'save' button was clicked, save all data
            if (request.post) {
                // Save all event date related data
                bindData(eventDate, params, [include: ["yearCode", "startDate", "endDate", "dateAsText",
                        "description", "longDescription", "createStatistics"]], "EventDate")

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
                    flash.message = g.message(code: 'default.created.message', args: [g.message(code: 'eventDate.label'), eventDate.toString()])
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
        EventDate date = pageInformation.date
        
        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all event date related data
            bindData(date, params, [include: ['startDate', 'endDate',
                    'dateAsText', 'description', 'longDescription', 'createStatistics']], 'EventDate')

            // Get a list of all days for this event date
            // If they do not come up, they have to be deleted
            Set<Day> toBeDeleted = new HashSet<Day>(date.days)
            
            // Save all possible days
            int i = 0
            while (params."Day_${i}") {
                Day day = null

                // Try to find the day in the database
                Long id = params.long("Day_${i}.id")
                if (id) {
                    day = Day.findById(id)
                }

                // Otherwise create a new one
                if (!day) {
                    day = new Day()
                }
                
                // Save the day, add it to the event date and remove it from the deletion list
                bindData(day, params, [include: ['dayNumber', 'day']], "Day_${i}")
                date.addToDays(day)
                toBeDeleted.remove(day)
                i++
            }

            // Everything left in the deletion list must be deleted
            toBeDeleted.each { day ->
                day.softDelete()
                day.save()
            }

            // Save the event date and redirect to the previous page if everything is ok
            if (date.save(flush: true)) {
                flash.message = g.message(code: 'default.updated.message', args: [g.message(code: 'eventDate.label'), date.toString()])
                if (params['btn_save_close']) {
                    redirect(uri: eca.createLink(previous: true, noBase: true))
                    return
                }
            }
        }
        
        // Show all event date related information
        render(view: "form", model: [eventDate: date])
    }
}
