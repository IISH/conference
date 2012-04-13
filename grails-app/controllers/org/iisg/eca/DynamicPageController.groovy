package org.iisg.eca

import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty

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
            // TODO: export service needs update
            exportService.getPage(params.format, response, null, params.sep, page.toString())
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
            // Get the element from which this action was called
            PageElement element = dynamicPage.elements.get(params.int('eid'))

            // This element should be a form...
            if (element?.type == PageElement.Type.FORM) {
                ViewElement form = (ViewElement) element
                DynamicPageResults results = new DynamicPageResults(form, params)
                Set<Column> hasChildren = new HashSet<Column>()

                // Loop over all domain classes, binding the data from ONLY those columns specified in the dynamic page
                form.allDomainClasses.each { domainClass ->
                    Set<Column> columns = form.getAllColumnsForDomainClass(domainClass)

                    if (columns.grep { it.parent?.multiple }.isEmpty()) {
                        bindData(results.get(domainClass.name), params, [include: columns.collect { it.name }], domainClass.name)
                    }
                    else {
                        int i = 1
                        while (params."${domainClass.name}_${i}") {
                            bindData(results.get("${domainClass.name}_${i}"), params, [include: columns.collect { it.name }], "${domainClass.name}_${i}")
                            i++
                        }
                        results.remove(domainClass.name)
                    }

                    hasChildren.addAll(columns.grep { it.hasChildren() })
                }

               List<Object> resultsToSave = results.get().values() as List

                hasChildren.each { c ->
                    Object[] owningSide = [results.get(c.domainClass.name)]
                    Object[] otherSide
                    GrailsDomainClassProperty owner = c.property

                    if (c.multiple) {
                        otherSide = results.get(c.property.referencedDomainClass.name)
                    }
                    else {
                        otherSide = [results.get(c.property.referencedDomainClass.name)]
                    }

                    if (!c.property.owningSide) {
                        owningSide = otherSide
                        otherSide = [results.get(c.domainClass.name)]
                        owner = c.property.otherSide
                    }

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

                    otherSide.each { resultsToSave.remove(it) }
                }

                resultsToSave.each { it.save() }

                // If validation fails, return the page and show the errors
                if (!results.get().values().grep { it.hasErrors() }.isEmpty()) {
                    render(view: '../layouts/content.gsp', model: [page: dynamicPage, content: dynamicPageService.getTemplate(dynamicPage, params, results)])
                    return
                }

                flash.message = message(code: "default.successful.message")

                // Redirect to previous page
                redirect(controller: params.prevController, action: params.prevAction, id: params.prevId)
            }
            else {
                render(view: '../layouts/content.gsp', model: [page: dynamicPage, content: dynamicPageService.getTemplate(dynamicPage, params)])
            }
        }
    }
}
