package org.iisg.eca

import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 * Domain class of table holding all dynamic pages
 */
class DynamicPage {
    private static final XmlSlurper XML_SLURPER = new XmlSlurper(false, false)

    String content
    String cache
    Page page
    EventDate date

    Map elements

    static belongsTo = [Page, EventDate]
    static transients = ['elements']

    static constraints = {
        content blank: false,   maxSize: 65540
        cache   nullable: true, maxSize: 65540
        date    nullable: true
    }

    static mapping = {
		table 'dynamic_pages'
		version false

        id      column: 'dynamic_page_id'
        content column: 'content',      sqlType: 'longtext'
        cache   column: 'cache',        sqlType: 'longtext'
        page    column: 'page_id'
        date    column: 'date_id'
	}

    /**
     * Returns a handler for parsing the xml content describing all page elements
     * @return A handler for parsing the xml content
     */
    GPathResult getXml() {
        XML_SLURPER.parseText("<views>${content}</views>")
    }

    /**
     * Obtains the data from the database required to fill out the forms, tables and overviews
     * @param params All parameters send with this request
     * @return A map of results linked to the corresponding element id
     */
    Map<Integer, DynamicPageResults> getResults(GrailsParameterMap params) {
        Map<Integer, DynamicPageResults> results = new HashMap<Integer, DynamicPageResults>()
        elements.each { eid, element ->
            if (element.type != PageElement.Type.BUTTONS) {
                results.put(eid, new DynamicPageResults((ViewElement) element, params))
            }
        }
        results
    }
}
