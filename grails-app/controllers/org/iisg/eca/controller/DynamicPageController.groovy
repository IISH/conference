package org.iisg.eca.controller

import org.iisg.eca.domain.DynamicPage

import org.iisg.eca.dynamic.DataContainer
import org.iisg.eca.dynamic.DynamicPageResults

/**
 *  Default controller for all dynamic pages
 */
class DynamicPageController {
    /**
     * Service responsible for all dynamic pages related actions
     */
    def dynamicPageService

    /**
     * Service responsible for saving the results to the database
     */
    def dynamicPagePostService
    
    /**
     * Service responsible for exporting results into a different format
     */
    def exportService

    /**
     * This action is only responsible for displaying a particular dynamic page
     */
    def get() {
        DynamicPage dynamicPage = dynamicPageService.getDynamicPage()

        if (params.format) {
            DataContainer exportContainer = (DataContainer) dynamicPage.elements.find { (it instanceof DataContainer) && (it.eid == params.int('export')) }
            DynamicPageResults exportResults = new DynamicPageResults(exportContainer, params)
            exportService.getPage(params.format, response, exportContainer.allColumns, exportResults.get(), params.sep)
            return
        }

        render(view: '../layouts/content.gsp', model: [page: dynamicPage, content: dynamicPageService.getTemplate(dynamicPage, params)])
    }

    /**
     * This action is responsible for both displaying a particular dynamic page and for persisting any changes to the database
     */
    def getAndPost() {
        DynamicPage dynamicPage = dynamicPageService.getDynamicPage()

        if (request.get) {
            render(view: '../layouts/content.gsp', model: [page: dynamicPage, content: dynamicPageService.getTemplate(dynamicPage, params)])
        }
        else if (request.post) {
            DynamicPageResults results = dynamicPagePostService.saveFormData(dynamicPage.elements.find { it.eid == params.int('eid') }, params)
            
            // If there are no results, it was most likely not a form; just return the same page...
            if (!results) {
               render(view: '../layouts/content.gsp', model: [page: dynamicPage, content: dynamicPageService.getTemplate(dynamicPage, params)])
               return
            }
            
            // If validation fails, return the page and show the errors
            if (!results.get().grep { it.hasErrors() }.isEmpty()) {
                render(view: '../layouts/content.gsp', model: [page: dynamicPage, content: dynamicPageService.getTemplate(dynamicPage, params, results)])
                return
            }

            // Everything is fine, so redirect to either the previous page or the default page
            flash.message = message(code: "default.successful.message")

            if (params.prevController) {
                // Redirect to previous page
                redirect(uri: eca.createLink(previous: true, noBase: true))
            }
            else {
                // Redirect to index page
                redirect(uri: eca.createLink(controller: 'event', action: 'index', noBase: true))
            }
        }
    }
}
