package org.iisg.eca.tags

import org.codehaus.groovy.grails.web.util.WebUtils

/**
 * Tag library for creating links
 */
class LinksTagLib {
    /**
     * Information about the current page
     */
    def static pageInformation

    static namespace = "eca"

    /**
     * An alternative to the g:link tag for easier use with different events
     * and for implementation of previous page information
     */
    def link = { attrs, body ->
        out << g.link(linkPrep(attrs, params, session), body)
    }

    /**
     * An alternative to the g:createLink tag for easier use with different events
     * and for implementation of previous page information
     */
    def createLink = { attrs ->
        out << g.createLink(linkPrep(attrs, params, session))
    }

    /**
     * An alternative to the g:link tag for a link to the same page with all existing parameters + parameters added
     */
    def linkAllParams = { attrs, body ->
        out << g.link(linkAllParamsPrep(attrs, params, request, session), body)
    }

    /**
     * An alternative to the g:link tag for a link to the same page with all existing parameters + parameters added
     */
    def createLinkAllParams = { attrs ->
        out << g.createLink(linkAllParamsPrep(attrs, params, request, session))
    }

    /**
     * Prepares the link attributes
     * @param attrs The attributes of the called tag
     * @param params The request parameters
     * @param session The session object
     * @return A prepared attrs object
     */
    private static Map linkPrep(attrs, params, session) {
        // Make sure the parameters map exists
        if (!attrs.params) {
            attrs.params = [:]
        }

        // In cases when there are multiple bases added, one is enough...
        if (attrs.noBase) {
            attrs.base = '/.'
            attrs.remove('noBase')
        }

        // Then set the event and event date attributes
        attrs = setEventDateAttrs(attrs, params)

        // Set previous page info if the user wants to return to this page
        attrs = setPreviousInfoAttrs(attrs)

        // Finally link to the previous page, if that has to happen
        attrs = setPreviousAttrs(attrs, params, session)

        attrs
    }

    /**
     * Prepares the link attributes, including all parameters
     * @param attrs The attributes of the called tag
     * @param params The request parameters
     * @param request The request object
     * @param session The session object
     * @return A prepared attrs object
     */
    private static Map linkAllParamsPrep(attrs, params, request, session) {
        // Make sure the parameters map exists
        if (!attrs.params) {
            attrs.params = [:]
        }

        // Add event (date) information
        attrs.params.event = params.event
        attrs.params.date = params.date
        attrs.mapping = 'eventDate'

        // Only take parameters from the query string
        Map filteredParams = [:]
        if (request.queryString) {
            filteredParams = WebUtils.fromQueryString(request.queryString)
        }
        filteredParams.putAll(attrs.params)
        attrs.params = filteredParams

        // Make sure to set previous page information
        attrs = setPreviousInfoAttrs(attrs)

        attrs
    }

    private static Map setPreviousAttrs(attrs, params, session) {
        // Set previous URL if necessary
        if (attrs.previous) {
            def backParams = null

            // Do we have the code of the previous page?
            if (params.back) {
                // See if we can get the parameters of the previous page
                backParams = session.getValue(params.back)?.clone()

                // If this is an 'edit' page, go back two pages
                if (!attrs.forceOnePageBack && (params.action == 'edit') && backParams?.back) {
                    backParams = session.getValue(backParams.back)?.clone()
                }

                // If we have the parameters of the previous page, use them to recreate the link
                if (backParams) {
                    attrs.controller = backParams.controller
                    attrs.action = backParams.action
                    attrs.id = backParams.id
                    attrs.params = backParams
                }
            }

            // The default back URL, if we do not have the right properties
            if (!backParams) {
                attrs.action = 'index'
            }
        }

        // We know we have to go to the previous page, so remove the attribute
        attrs.remove('previous')

        attrs
    }

    private static Map setEventDateAttrs(attrs, params) {
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

        // We have set the event and date, so we do not need these attributes anymore
        attrs.remove('event')
        attrs.remove('date')

        attrs
    }

    private static Map setPreviousInfoAttrs(attrs) {
        // Set previous page identifier
        if (!attrs.noPreviousInfo) {
            attrs.params.back = pageInformation.sessionIdentifier
        }

        // We know whether to set previous info, so remove it
        attrs.remove('noPreviousInfo')

        attrs
    }
}
