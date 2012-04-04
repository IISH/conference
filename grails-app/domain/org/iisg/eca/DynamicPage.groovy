package org.iisg.eca

import groovy.util.slurpersupport.GPathResult

/**
 * Domain class of table holding all dynamic pages
 */
class DynamicPage {
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

    GPathResult getXml() {
        new XmlSlurper(false, false).parseText("<views>${content}</views>")
    }
}
