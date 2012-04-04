package org.iisg.eca

import groovy.util.slurpersupport.GPathResult

/**
 * Service responsible for dynamic page related actions
 */
class DynamicPageService {
    /**
     * grailsApplication gives us the possibility to search for specific domain classes
     */
    def grailsApplication
    /**
     * groovyPagesTemmplateEngine allows us to render the dynamic pages
     */
    def groovyPagesTemplateEngine

    private static final FORM =     'element:form'
    private static final TABLE =    'element:table'
    private static final OVERVIEW = 'element:overview'
    private static final BUTTONS =  'element:buttons'
    private static final COLUMN =   'element:column'
    private static final BUTTON =   'element:button'

    /**
     * Returns the dynamic page for a certain <code>Page</code> if available, with all described elements
     * @param page The requested page
     * @param params All parameters from the request
     */
    def getDynamicPage(Page page, params) {
        // Find out if there is a dynamic page linked to this page
        if (page.dynamicPages.size() != 1) {
            return null
        }

        // Parse the xml content for the elements
        def dynamicPage = page.dynamicPages.toArray()[0]
        dynamicPage.elements = gePageElements(dynamicPage.xml, params)
        dynamicPage
    }

    /**
     * Returns a template of the dynamic page for the current request
     * @param dynamicPage A dynamic page with the described elements set
     * @return A template
     */
    def getTemplate(DynamicPage dynamicPage) {
        // If there is no cache found in the database, create it now
        if (!dynamicPage.cache) {
            def tmpl = groovyPagesTemplateEngine.createTemplate(dynamicPage.content, 'temp')
            def out = new StringWriter()

            tmpl.make().writeTo(out)

            dynamicPage.cache = out.toString()
            dynamicPage.save(flush: true)
        }

        // Use the cache to fill the elements on the page with the requested data from the database
        def tmpl = groovyPagesTemplateEngine.createTemplate(dynamicPage.cache, 'temp')
        def out = new StringWriter()

        tmpl.make([page: dynamicPage]).writeTo(out)
        out.toString()
    }

    /**
     * Parses the dynamic page in search of page elements
     * @param xml The xml representing the dyanamic page
     * @param params The parameters of the current request
     * @return A map of all elements of the dynamic page linked to their eid as the key
     */
    def private gePageElements(GPathResult xml, params) {
        def elements = new HashMap()
        def eid = 0

        // Loop over all the elements on the page
        xml.children().each { element ->
            // Create a new map to store all the columns requested by domain class
            def columns = new HashMap<String, List>()
            def result
            def type

            // Find out the main domain class of this page element
            def mainDomainClass = element.@domain.text()
            (mainDomainClass.isEmpty()) ?: columns.put(mainDomainClass, new ArrayList())

            // If there is a main domain class, loop over its children
            // to find out which columns are requested from this domain class and save it to the map
            if (!mainDomainClass.isEmpty()) {
                element.children().each { c ->
                    if (c.name().equals(COLUMN) && !c.@name.text().isEmpty()) {
                        def domainClass = mainDomainClass
                        // If one of the children also specifies another domain class,
                        // then create a new entry in the map for this domain class in case it doesn't exist yet
                        if (!c.@domain.text().isEmpty()) {
                            domainClass = c.@domain.text()
                            if (!columns.containsKey(c.@domain.text())) {
                                columns.put(c.@domain.text(), new ArrayList())
                            }
                        }
                        columns.get(domainClass).add(c.@name.text())
                    }
                }
            }

            // Now that we now which domain classes and columns are requested, find out how to query the data
            // In case of a table, if there is a query present, use that;
            // if not, then obtain a list of all the data in the database for the main domain class
            if (element.name().equals(TABLE)) {
                type = PageElement.Type.TABLE
                def query = element.@query.text()

                if (query.isEmpty()) {
                    result = getListForDomain(mainDomainClass, columns.get(mainDomainClass), element, params)
                }
                else {
                    result = getListForQuery(query, mainDomainClass, element, params)
                }

                // All the information is obtained, return a new PageElement with the results linked to an element id
                elements.put(eid++, new PageElement(type, columns, result))
            }
            // In case of a form or overview, get one instance back based on the parameters
            else if (element.name().equals(FORM) || element.name().equals(OVERVIEW)) {
                type = (element.name().equals(FORM)) ? PageElement.Type.FORM : PageElement.Type.OVERVIEW
                result = getInstanceWithCriteria(mainDomainClass, columns, element, params)
                elements.put(eid++, new PageElement(type, columns, result))
            }
        }

        elements
    }

    /**
     * Returns a list of all data in the database for the specified main domain class
     * @param mainDomainClass The domain class to query on for a list of data
     * @param columns A list of the columns, from which some may need to be filtered or sorted
     * @param element The part of the xml describing the element
     * @param params The parameters of the current request
     * @return A list of all data in the database for the specified main domain class
     */
    def private getListForDomain(mainDomainClass, columns, element, params) {
        def criteria = grailsApplication.getDomainClass("org.iisg.eca.${mainDomainClass}").clazz.createCriteria()
        def max = !element.@max.text().isEmpty() ?: element.@max.text()
        def offset = params.offset?.toString()

        criteria.list {
            //
            and {
                // Loop over all the columns and place filters if needed
                columns.each { c ->
                    def filter = params["filter0_${c}"]
                    def sort = params["sort0_${c}"]

                    // Place a filter on this column, if specified by the user
                    if (filter && !filter.isEmpty()) {
                        filter = filter.split()
                        filter = "%${filter.join('%')}%"
                        like(c, filter.toString())
                    }

                    // Sort this column, if specified by the user
                    if (sort && !sort.isEmpty()) {
                        order(c, sort)
                    }
                }
            }

            // For pagination, find out the maximum number of results and the offset.
            // If not specified, return at most 100 results starting from the top of the list
            firstResult(offset ?: 0)
            maxResults(max ?: 100)
        }
    }

    /**
     * Returns a list of all data in the database for the specified query
     * @param query The query in question
     * @param mainDomainClass The domain class to query on for a list of data
     * @param element The part of the xml describing the element
     * @param params The parameters of the current request
     * @return A list of all data in the database for the specified main domain class
     */
    def private getListForQuery(query, mainDomainClass, element, params) {
        def max = !element.@max.text().isEmpty() ?: element.@max.text()
        def offset = params.offset?.text()

        // TODO
    }

    /**
     * Returns an instance from the database, based on a specified element
     * @param mainDomainClass The main domain class
     * @param columns A map of all the columns
     * @param element The element in question
     * @param params The parameters of the current request
     * @return A list of instances, currently with only one result
     */
    def private getInstanceWithCriteria(mainDomainClass, columns, element, params) {
        // In the future, we might need to return more instances
        def instances = []
        def id = element.@id?.text()

        // If the id is specified in the url, get it from the parameters
        // Otherwise, assume it is a number
        if (id) {
            id = (id == 'url') ? params.long('id') : String.toLong(id)
        }

        columns.keySet().each { domainClass ->
            def instance = grailsApplication.getDomainClass("org.iisg.eca.${domainClass}")

            if (domainClass == mainDomainClass) {
                instance = (id) ? instance.clazz.get(id) : instance.newInstance()
            }
            else {
                //def propName = instance.clazz.declaredFields[0].find { it.type.simpleName.equalsIgnoreCase(mainDomainClass) }
                instance = /*(id) ? instance.clazz.find { "${propName}.id" == id } : */instance.newInstance()
            }

            instances << instance
        }

        instances
    }
}
