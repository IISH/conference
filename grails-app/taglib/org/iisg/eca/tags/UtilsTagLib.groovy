package org.iisg.eca.tags

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import groovy.xml.MarkupBuilder
import org.iisg.eca.domain.User
import org.iisg.eca.domain.Event
import org.iisg.eca.domain.EventDate

/**
 * Utilities tag library
 */
class UtilsTagLib {
    def pageInformation
    def springSecurityService

    static namespace = "eca"

    /**
     * Tag printing all roles of the logged in user in a user-friendly way
     */
    def roles = {
        String userRoles = SpringSecurityUtils.getPrincipalAuthorities().collect { it.authority }.join(', ')
        if (!userRoles.isEmpty()) {
            out << "(${userRoles})"
        }
    }

    /**
     * Creates an event switcher to make switching between different events and their dates easier
     */
    def eventSwitcher = {
        MarkupBuilder builder = new MarkupBuilder(out)
        builder.doubleQuotes = true

        Set<EventDate> dates = User.get(springSecurityService.principal.id).dates
        List<EventDate> datesSorted = new ArrayList()
        EventDate.sortByEventAndDate.list().each { date ->
            if (dates.contains(date)) {
                datesSorted.add(date)
            }
        }

        Map<Event, EventDate> datesByEvent = [:]
        datesSorted.each { date ->
            List<EventDate> list = (List<EventDate>) datesByEvent.get(date.event, new ArrayList<EventDate>())
            list.add(date)
        }

        builder.form(method: "get", action: "switchEvent") {
            builder.select(id: "event_switcher", name: "event_switcher") {
                datesByEvent.keySet().each { event ->
                    builder.optgroup(label: event.toString()) {
                        datesByEvent.get(event).each { date ->
                            if (pageInformation.date?.id == date?.id) {
                                builder.option(value: date.id, date.toString(), selected: "selected")
                            }
                            else {
                                builder.option(value: date.id, date.toString())
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * An alternative to the g:link tag for easier use with different events
     * and for implementation of previous page information
     */
    def link = { attrs, body ->
        if (!attrs.params) {
            attrs.params = [:]
        }

        // Add event (date) information
        if (attrs.event && attrs.date) {
            attrs.params.event = attrs.event
            attrs.params.date = attrs.date
            attrs.mapping = 'eventDate'
        }
        else if (params.event && params.date) {
            attrs.params.event = params.event
            attrs.params.date = params.date
            attrs.mapping = 'eventDate'
        }

        // Add parameters for moving back to the current page
        attrs.params.prevController = params.controller
        attrs.params.prevAction = params.action
        if (params.id) {
            attrs.params.prevId = params.id
        }

        out << g.link(attrs, body)
    }

    /**
     * An alternative to the g:link tag for a link to the same page with all existing parameters + parameters added
     */
    def linkAllParams = { attrs, body ->
        attrs.controller = params.controller
        attrs.action = params.action
        attrs.id = params.id

        // Add event (date) information
        attrs.params.event = params.event
        attrs.params.date = params.date
        attrs.mapping = 'eventDate'

        Map tempParams = [:]
        tempParams.putAll(params)
        tempParams.putAll(attrs.params)
        attrs.params = tempParams

        out << g.link(attrs, body)
    }

    /**
     * If the message is not found in the language properties files, try the fall back code and attributes.
     * If the message is still not found, then fall back to the default code
     */
    def fallbackMessage = { attrs ->
        String msg = message(code: attrs.code, attrs: attrs.attrs)

        if (msg.equals(attrs.code)) {
            msg = message(code: attrs.fbCode, attrs: attrs.fbAttrs, default: attrs.default)
        }

        out << msg
    }
}
