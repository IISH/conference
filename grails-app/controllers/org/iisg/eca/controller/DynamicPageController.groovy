package org.iisg.eca.controller

import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.iisg.eca.dynamic.DynamicPageResults
import org.iisg.eca.dynamic.DataContainer
import org.iisg.eca.dynamic.Column
import org.iisg.eca.domain.DynamicPage

/**
 *  Default controller for all dynamic pages
 */
class DynamicPageController {
    /**
     * Service responsible for all dynamic pages related actions
     */
    def dynamicPageService

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
            exportService.getPage(params.format, response, exportContainer.columns, exportResults.get(), params.sep)
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
        // TODO: Move to Service?
        else if (request.post) {
            // Get the element from which this action was called
            DataContainer element = (DataContainer) dynamicPage.elements.find { (it instanceof DataContainer) && (it.eid == params.int('eid')) }

            // This element should be a form...
            if (element?.type == DataContainer.Type.FORM) {
                DynamicPageResults results = new DynamicPageResults(element, params)
                Set<Column> hasChildren = new HashSet<Column>()

                // Loop over all domain classes, binding the data from ONLY those columns specified in the dynamic page
                element.allDomainClasses.each { domainClass ->
                    Set<Column> columns = element.getAllColumnsForDomainClass(domainClass)

                    // If there is only one instance, just save that one
                    if (columns.findAll { (it.parent instanceof Column) && (it.parent.multiple) }.isEmpty()) {
                        bindData(results.get(domainClass.name), params, [include: columns.collect { it.name }], domainClass.name)
                    }
                    else {
                        // Every new instance has a new number which simply adds up and the domain class name as a prefix
                        // Keep on saving every instance, until there are no values in the parameters map anymore with the given prefix
                        int i = 1
                        while (params."${domainClass.name}_${i}") {
                            Long id = params.long("${domainClass.name}_${i}.id")
                            bindData(results.get(domainClass.name, id), params, [include: columns.collect { it.name }], "${domainClass.name}_${i}")
                            i++
                        }
                    }

                    // Save those columns with children for later, we need to combine the results later on
                    hasChildren.addAll(columns.grep { it.hasElements() })
                }

                // For all those results, create a new list, so we can filter out which objects need to be saved
                List<Object> resultsToSave = results.get() as List

                // Explore the relationships of every column and combine the results
                hasChildren.each { c ->
                    Object[] owningSide = results.getArray(c.domainClass.name)
                    Object[] otherSide = results.getArray(c.property.referencedDomainClass.name)
                    GrailsDomainClassProperty owner = c.property

                    // Make sure the owning side actually is the owning side, if not, switch them around
                    if (!c.property.owningSide) {
                        owningSide = otherSide
                        otherSide = results.getArray(c.domainClass.name)
                        owner = c.property.otherSide
                    }

                    // Add the other side to the owning side, either to a collection or not
                    if (owner.oneToOne || owner.manyToOne) {
                        owningSide.each { dc ->
                            otherSide.each { os ->
                                dc[owner.name] = os
                            }
                        }
                    }
                    else if (owner.manyToMany || owner.oneToMany) {
                        owningSide.each { dc ->
                            otherSide.each { os ->
                                dc."addTo${owner.name[0].toUpperCase() + owner.name[1..-1]}"(os)
                            }
                        }
                    }

                    // The owning side needs to be saved and takes care of the relationship
                    // So remove the other side from the list of results to save
                    otherSide.each { resultsToSave.remove(it) }
                }

                // We're done, save the results that have to be saved
                resultsToSave.each { it.save() }

                // If validation fails, return the page and show the errors
                if (!results.get().grep { it.hasErrors() }.isEmpty()) {
                    render(view: '../layouts/content.gsp', model: [page: dynamicPage, content: dynamicPageService.getTemplate(dynamicPage, params, results)])
                    return
                }

                flash.message = message(code: "default.successful.message")

                if (params.prevController) {
                    // Redirect to previous page
                    redirect(controller: params.prevController, action: params.prevAction, id: params.prevId)
                }
                else {
                    // Redirect to index page
                    redirect(controller: 'event', action: 'list')
                }
            }
            else {
                render(view: '../layouts/content.gsp', model: [page: dynamicPage, content: dynamicPageService.getTemplate(dynamicPage, params)])
            }
        }
    }
}
