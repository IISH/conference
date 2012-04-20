package org.iisg.eca.tags

import org.iisg.eca.domain.Page

/**
 * Temporary tag library for listing all pages in the database in the menu
 */
class TempPagesTagLib {
    def menu = { attrs, body ->
        def pages = Page.list(sort: 'controller')
        def curController = null
        pages.each { page ->
            if (!curController) {
                curController = page.controller
            }
            if (curController != page.controller) {
                curController = page.controller
                out << "<dd> </dd>"
            }
            out << "<dd>${eca.link(controller: page.controller, action: page.action, page.toString())}</dd>"
        }
    }
}
