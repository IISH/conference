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
     * This action is responsible for both displaying a particular dynamic page and for persisting any changes to the database
     */
    def dynamic() {
        DynamicPage dynamicPage = dynamicPageService.getDynamicPage()

        // If one of the data containers needs an id from the url, while there isn't one specified
        // Send back with a 'no id' message
        if ((params.id == null) && dynamicPage.elements.find { (it instanceof DataContainer) && (it.id == 'url') }) {
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // Get the results
        Map<Integer, DynamicPageResults> allResults = dynamicPage.getResults(params)

        // If one of the data containers did not find a record with the specified id, send back a message
        DynamicPageResults nullResults = allResults.values().find { it.containsNullResults() }
        if (nullResults) {
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: "${nullResults.dataContainer.domainClass.propertyName}.label")])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        if (request.get) {
            // If an export of the results is requested, try to delegate the request to the export service
            if (params.format) {
                DataContainer exportContainer = (DataContainer) dynamicPage.elements.find { (it instanceof DataContainer) && (it.eid == params.int('export')) }
                DynamicPageResults exportResults = new DynamicPageResults(exportContainer, params)
                exportService.getPage(params.format, response, exportContainer.allColumns, exportResults.get(), params.sep)
                return
            }

            render(view: '../layouts/content.gsp', model: [page: dynamicPage, content: dynamicPageService.getTemplate(dynamicPage, params, allResults)])
        }
        else if (request.post) {
            // Find out from which element the post request was made using the eid (element id)
            DataContainer container = (DataContainer) dynamicPage.elements.find { (it instanceof DataContainer) && (it.eid == params.int('eid')) }
            // If found, try to save the filled out data
            DynamicPageResults results = dynamicPagePostService.saveFormData(container, params)
            
            // If there are no results, it was most likely not a form; just return the same page
            // Otherwise, overwrite the results of the element that was just saved with new information
            if (!results) {
               render(view: '../layouts/content.gsp', model: [page: dynamicPage, content: dynamicPageService.getTemplate(dynamicPage, params, allResults)])
               return
            }
            else {
                allResults.put(params.int('eid'), results)
            }
            
            // If validation fails, return the page and show the errors
            if (!results.get().grep { it.hasErrors() }.isEmpty()) {
                render(view: '../layouts/content.gsp', model: [page: dynamicPage, content: dynamicPageService.getTemplate(dynamicPage, params, allResults)])
                return
            }

            // Everything is fine, so redirect to either the previous page or the default page
            flash.message = g.message(code: "default.successful.message")

            // Redirect to previous page
            redirect(uri: eca.createLink(previous: true, noBase: true))
        }
    }
}
