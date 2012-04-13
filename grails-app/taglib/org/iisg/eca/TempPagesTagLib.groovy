package org.iisg.eca

/**
 * Temporary tag library for listing all pages in the database in the menu
 */
class TempPagesTagLib {
    def menu = { attrs, body ->
        def pages = Page.list()
        pages.each {    page ->
            out << "<dd>${link(controller: page.controller, action: page.action, page.toString())}</dd>"
        }
    }
}
