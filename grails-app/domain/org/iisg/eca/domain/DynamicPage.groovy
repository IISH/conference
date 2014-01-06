package org.iisg.eca.domain

import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import org.iisg.eca.dynamic.DynamicPageResults
import org.iisg.eca.dynamic.DataContainer
import org.iisg.eca.dynamic.ContainerElement

/**
 * Domain class of table holding all dynamic pages
 */
class DynamicPage extends EventDateDomain {
    private static final XmlSlurper XML_SLURPER = new XmlSlurper(false, false)

    String content
    String cache
    Page page
    
    List<ContainerElement> elements

    static belongsTo = Page
    static transients = EventDateDomain.transients + ['elements']

    static constraints = {
        content blank: false,   maxSize: 65540
        cache   nullable: true, maxSize: 65540
    }

    static mapping = {
        table 'dynamic_pages'
        version false

        id      column: 'dynamic_page_id'
        content column: 'content',      sqlType: 'longtext'
        cache   column: 'cache',        sqlType: 'longtext'
        page    column: 'page_id'
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
        elements.each { element ->
            if (element instanceof DataContainer ) {
                results.put(element.eid, new DynamicPageResults(element, params))
            }
        }
        results
    }
}
