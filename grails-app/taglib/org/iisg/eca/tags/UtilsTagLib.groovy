package org.iisg.eca.tags

import org.iisg.eca.domain.Page
import org.iisg.eca.domain.User
import org.iisg.eca.domain.Event
import org.iisg.eca.domain.EventDate

import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Utilities tag library
 */
class UtilsTagLib {
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
     * @attr ids An array of id's to use for navigation
     */
    def navigation = { attrs -> 
        MarkupBuilder builder = new MarkupBuilder(out)
        builder.doubleQuotes = true
        
        builder.div(class: "navigation") {
            def prev = attrs.prev
            def next = attrs.next

            // If there is an array of ids given, use that for navigation
            if (attrs.ids) {
                def index = null

                // Find the index value of the current id
                attrs.ids.eachWithIndex { curId, i ->
                    if (curId == params.id.toLong()) {
                        index = i
                    }
                }

                // Explicitly check if the index is not null, the index is allowed to be 0
                prev = ((index != null) && (index-1 >= 0)) ? attrs.ids.get(index-1) : null
                next = ((index != null) && (index+1 < attrs.ids.size())) ? attrs.ids.get(index+1) : null
            }

            // Build prev link, if it exists
            if (prev) {
                builder."a"(class: "prev", href: createLinkAllParams(action: params.action, id: prev, setPrev: true)) {
                    builder.span(class: "ui-icon ui-icon-arrowthick-1-w", "")                    
                    builder.span(g.message(code: "default.paginate.prev"))
                }
            }
            
            builder.span(class: "ui-icon ui-icon-bullet", "")

            // Build next link, if it exists
            if (next) {
                builder."a"(class: "next", href: createLinkAllParams(action: params.action, id: next, setPrev: true)) {
                    builder.span(g.message(code: "default.paginate.next"))
                    builder.span(class: "ui-icon ui-icon-arrowthick-1-e", "")
                }
            }
        }
    }

    /**
     * Tag creating a boolean select box
     * @attrs value The selected value
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
     * Prints the menu
     */
    def menu = { attrs ->
        MarkupBuilder builder = new MarkupBuilder(out)
        builder.doubleQuotes = true

        printSubMenu(builder, Page.menuPages.list(), attrs.date)
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
        out << g.link(linkAllParamsPrep(attrs, params), body)
    }

    /**
     * An alternative to the g:link tag for a link to the same page with all existing parameters + parameters added
     */
    def createLinkAllParams = { attrs ->
        out << g.createLink(linkAllParamsPrep(attrs, params))
    }


    /**
     * If the message is not found in the language properties files, try the fall back code and attributes.
     * If the message is still not found, then fall back to the default code
     */
    def fallbackMessage = { attrs ->
        String msg = message(code: attrs.code, attrs: attrs.attrs).toString()

        if (msg.equals(attrs.code.toString())) {
            msg = message(code: attrs.fbCode, attrs: attrs.fbAttrs, default: attrs.default).toString()
        }

        out << msg
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
     * The body is only executed if the user has access to the page the attributes point to
     */
    def ifUserHasAccess = { attrs, body ->
        Page page = Page.findByControllerAndAction(attrs.controller, attrs.action)

        if (page.hasAccess()) {
            out << body()
        }
    }

    /**
     * The body is only executed if the user has no access to the page the attributes point to
     */
    def ifUserHasNoAccess = { attrs, body ->
        Page page = Page.findByControllerAndAction(attrs.controller, attrs.action)

        if (!page.hasAccess()) {
            out << body()
        }
    }

    /**
     * Prepares the link attributes
     * @param attrs The attributes of the called tag
     * @param params The request parameters
     * @return A prepared attrs object
     */
    private static Map linkPrep(attrs, params) {
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

    private static Map linkAllParamsPrep(attrs, params) {
        if (!attrs.params) {
            attrs.params = [:]
        }

        // Add event (date) information
        attrs.params.event = params.event
        attrs.params.date = params.date
        attrs.mapping = 'eventDate'

        Map tempParams = [:]
        tempParams.putAll(params)
        tempParams.putAll(attrs.params)
        attrs.params = tempParams

        // Set previous info
        if (attrs.setPrev) {
            attrs.params.prevController = params.controller
            attrs.params.prevAction = params.action

            if (params.id) {
                attrs.params.prevId = params.id
            }
        }
        attrs.remove('setPrev')

        attrs
    }

    /**
     * Prints the menu and its sub menus
     * @param builder The markup builder for the HTML output
     * @param pages The pages to place in the menu
     */
    private void printSubMenu(MarkupBuilder builder, Collection<Page> pages, EventDate date) {
        for (Page page : pages) {
            if (page.hasAccess()) {
                if (page.controller && page.action) {
                    builder.dd {
                        def a = 1

                        if (page.controller.equals('eventDate') && page.action.equals('show')) {
                            builder.mkp.yieldUnescaped(eca.link(controller: page.controller, action: page.action, id: date?.id, page.toString()))
                        }
                        else {
                            builder.mkp.yieldUnescaped(eca.link(controller: page.controller, action: page.action, page.toString()))
                        }
                    }
                }
                else {
                    builder.dd {
                        builder.a(href: "#", page.toString())
                    }
                }

                // If there is a sub menu, print it as well
                if (page.subPages?.size() > 0) {
                    builder.dl {
                        printSubMenu(builder, Page.subMenuPages(page).list(), date)
                    }
                }
            }
        }
    }
}
