package org.iisg.eca

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Utilities tag library
 */
class UtilsTagLib {
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
        }
        else if (params.event && params.date) {
            attrs.params.event = params.event
            attrs.params.date = params.date
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
