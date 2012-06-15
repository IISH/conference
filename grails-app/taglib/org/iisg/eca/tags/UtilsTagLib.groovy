package org.iisg.eca.tags

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Event
import org.iisg.eca.domain.EventDate

import groovy.xml.MarkupBuilder

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Utilities tag library
 */
class UtilsTagLib {
    def pageInformation
    def springSecurityService

    static namespace = "eca"

    /**
     * Tag responsible for formatting the given String
     * @attr text The text to format
     */
    def formatText = { attrs ->
        if (attrs.text != null && (attrs.text.toString().trim().length() != 0)) {
            out << attrs.text.encodeAsHTML().replaceAll("\n", "<br />").replaceAll("\t", "     ")
        }
        else {
            out << "-"
        }
    }
    
    /**
     * Tag creating navigation buttons
     * @attr prev The url to go to when the user wants to go to the previous page
     * @attr next The url to go to when the user wants to go to the next page
     */
    def navigation = { attrs -> 
        MarkupBuilder builder = new MarkupBuilder(out)
        builder.doubleQuotes = true
        
        builder.div(class: "navigation") {
            if (attrs.prev) {
                builder.a(class: "prev", href: attrs.prev) {                    
                    builder.span(class: "ui-icon ui-icon-arrowthick-1-w", "")                    
                    builder.span(g.message(code: "default.paginate.prev"))
                }
            }
            
            builder.span(class: "ui-icon ui-icon-bullet", "")
            
            if (attrs.next) {
                builder.a(class: "next", href: attrs.next) {
                    builder.span(g.message(code: "default.paginate.next"))
                    builder.span(class: "ui-icon ui-icon-arrowthick-1-e", "")
                }
            }
        }
    }

    /**
     * Tag creating a boolean select box
     */
    def booleanSelect = { attrs ->
        MarkupBuilder builder = new MarkupBuilder(out)
        builder.doubleQuotes = true

        Map options =
            ["null": "${g.message(code: 'default.boolean.true')} & ${g.message(code: 'default.boolean.false')}",
            "0": "${g.message(code: 'default.boolean.false')}",
            "1": "${g.message(code: 'default.boolean.true')}"]
        String value = attrs.value
        attrs.remove("value")

        builder.select(attrs) {
            options.each { option ->
                if (option.key == value) {
                    builder.option(value: option.key, selected: "selected", option.value)
                }
                else {
                    builder.option(value: option.key, option.value)
                }
            }
        }
    }

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
    def eventSwitcher = { attrs ->
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

        builder.form(method: "get", action: eca.createLink(controller: 'event', action: 'switchEvent')) {
            builder.select(id: "event_switcher", name: "event_switcher") {
                datesByEvent.keySet().each { event ->
                    builder.optgroup(label: event.toString()) {
                        datesByEvent.get(event).each { date -> 
                            if (attrs.date?.id == date.id) {
                                builder.option(value: "${date.id}|${params.controller}", date.toString(), selected: "selected")
                            }
                            else {
                                builder.option(value: "${date.id}|${params.controller}", date.toString())
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
        out << g.link(linkPrep(attrs, params), body)
    }

    /**
     * An alternative to the g:createLink tag for easier use with different events
     * and for implementation of previous page information
     */
    def createLink = { attrs ->
        out << g.createLink(linkPrep(attrs, params))
    }

    /**
     * An alternative to the g:link tag for a link to the same page with all existing parameters + parameters added
     */
    def linkAllParams = { attrs, body ->
        if (!attrs.params) {
            attrs.params = [:]
        }

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

        out << msg.toString()
    }
    
    /**
     * Creates a date field with the given attributes
     */
    def dateField = { attrs -> 
        MarkupBuilder builder = new MarkupBuilder(out)
        builder.doubleQuotes = true
        
        attrs['type'] = "text"
        attrs['class'] = "${attrs['class']} datepicker"
        attrs['placeholder'] = g.message(code: 'default.date.form.format').toLowerCase()
        attrs['value'] = g.formatDate(formatName: 'default.date.form.format', date: attrs.date)
        attrs.remove('date')
        
        builder.input(attrs)
    }

    /**
     * Prepares the link attributes
     * @param attrs The attributes of the called tag
     * @param params The request parameters
     * @return A prepared attrs object
     */
    private static Object linkPrep(attrs, params) {
        if (!attrs.params) {
            attrs.params = [:]
        }

        // Add event (date) information
        if (attrs.event && attrs.date) {
            attrs.params.put('event', attrs.event)
            attrs.params.put('date', attrs.date)
            if (!attrs.mapping) {
                attrs.mapping = 'eventDate'
            }
        }
        else if (params.event && params.date) {
            attrs.params.put('event', params.event)
            attrs.params.put('date', params.date)
            if (!attrs.mapping) {
                attrs.mapping = 'eventDate'
            }
        }
        attrs.remove('event')
        attrs.remove('date')

        // Link back to the previous page
        if (attrs.previous && params.prevController) {
            attrs.controller = params.prevController
            attrs.action = params.prevAction ?: params.action
            attrs.id = params.prevId ?: params.id
            attrs.remove('previous')
        }
        else if (attrs.previous) {
            attrs.uri = '/'
            attrs.remove('previous')
        }

        // Add parameters for moving back to the current page, unless specifically specified not to do so
        if (!attrs.noPreviousInfo) {
            attrs.params.prevController = params.controller
            attrs.params.prevAction = params.action
            if (params.id) {
                attrs.params.prevId = params.id
            }
            attrs.remove('noPreviousInfo')
        }

        // In cases when there are multiple bases added, one is enough...
        if (attrs.noBase) {
            attrs.base = '/.'
            attrs.remove('noBase')
        }

        attrs
    }
}
