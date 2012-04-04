package org.iisg.eca

/**
 *  Default controller for all dynamic pages
 */
class DynamicPageController {
    /**
     * Service responsible for all dynamic pages related actions
     */
    def dynamicPageService

    /**
     * This action is only responsible for displaying a particular dynamic page
     */
    def get() {
        def page = Page.findByControllerAndAction(params.controller, params.action)
        DynamicPage dynamicPage = dynamicPageService.getDynamicPage(page, params)

        render(view: '../layouts/content.gsp', model: [page: dynamicPage, content: dynamicPageService.getTemplate(dynamicPage)])
    }

    /**
     * This action is responsible for both displaying a particular dynamic page and for persisting any changes to the database
     */
    def getAndPost() {
        def page = Page.findByControllerAndAction(params.controller, params.action)
        DynamicPage dynamicPage = dynamicPageService.getDynamicPage(page, params)

        if (request.get) {
            render(view: '../layouts/content.gsp', model: [page: dynamicPage, content: dynamicPageService.getTemplate(dynamicPage)])
        }
        else if (request.post) {
            // Get the element from which this action was called
            def form = dynamicPage.elements.get(params.int('eid'))

            // This element should be a form...
            if (form?.type == PageElement.Type.FORM) {
                // Loop over all domain classes, binding the data from ONLY those columns specified in the dynamic page
                form.result.each { result ->
                    def domainClassName = result.class.simpleName
                    def columns = form.getColumnsByDomainClassName(domainClassName)
                    bindData(result, params, [include: columns], domainClassName)
                    result.save(flush: true)
                }
            }
            else {
                render(view: '../layouts/content.gsp', model: [page: dynamicPage, content: dynamicPageService.getTemplate(dynamicPage)])
                return
            }

            // If validation fails, return the page and show the errors
            if (!form.result.grep { it.hasErrors() }.isEmpty()) {
                render(view: '../layouts/content.gsp', model: [page: dynamicPage, content: dynamicPageService.getTemplate(dynamicPage)])
                return
            }

            flash.message = message(code: "default.successful.message")

            // Find out if we need to redirect the user to a specific page after a succesful transaction.
            // If not found, redirect to the show page of the same controller
            def url = dynamicPage.defaultPage
            if (!dynamicPage.defaultPage) {
                url = "/${params.controller}/show"
            }

            redirect(uri: "${url}/${form.result[0].id}")
        }
    }
}
