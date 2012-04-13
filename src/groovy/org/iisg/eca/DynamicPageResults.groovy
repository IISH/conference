package org.iisg.eca

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class DynamicPageResults {
    private ViewElement viewElement
    private GrailsParameterMap params
    private def results
    
    DynamicPageResults(ViewElement viewElement, GrailsParameterMap params) {
        this.viewElement = viewElement
        this.params = params
        getResults()
    }
    
    def get() {
        results
    }
    
    def get(String domainClass) {
        Object[] result = results.values().findAll { it.class.simpleName == domainClass }

        if (result.length > 2) {
            return result
        }
        else if (result.length == 1) {
            return result[0]
        }
        else {
            String[] multiple = domainClass.split('_')
            String domainClassToClone = domainClass
            if (multiple.length > 1) {
                domainClassToClone = multiple[0..multiple.length-2].join('_')
            }
            results.get(domainClass, results.get(domainClassToClone).class.newInstance())
        }
    }

    void remove(String domainClass) {
        results.remove(domainClass)
    }
    
    private void getResults() {
        if (viewElement.type == PageElement.Type.TABLE) {
            if (viewElement.query) {
                getListForQuery() 
            }
            else {
                getList()
            }
        }
        else {                  
            getInstance()
        }   
    }  
    
    /**
     * Returns a list of all data in the database for the specified main domain class
     * @param mainDomainClass The domain class to query on for a list of data
     * @param columns A list of the columns, from which some may need to be filtered or sorted
     * @param element The part of the xml describing the element
     * @param params The parameters of the current request
     * @return A list of all data in the database for the specified main domain class
     */
    private void getList() {
        def criteria = viewElement.domainClass.clazz.createCriteria()
        String offset = params.offset?.toString()
        
        results = criteria.list {
            projections {
                viewElement.forAllColumnsWithChildren { c -> 
                    if (!c.parent) {
                        property("${c.name}.id")  
                    }
                    else {                        
                        groupProperty(c.path.join("."))
                    }
                }
            }
            
            and {
                // Loop over all the columns and place filters if needed
                viewElement.forAllColumns { c ->
                    String filter = params["filter_${viewElement.eid}_${c.name}"]
                    String sort = params["sort_${viewElement.eid}_${c.name}"]

                    // Place a filter on this column, if specified by the user
                    if (filter && !filter.isEmpty()) {
                        filter = filter.split()
                        filter = "%${filter.join('%')}%"
                        like(c.name, filter)
                    }

                    // Sort this column, if specified by the user
                    if (sort && !sort.isEmpty()) {
                        order(c.name, sort)
                    }
                }
            }

            // For pagination, find out the maximum number of results and the offset.
            // If not specified, return at most 100 results starting from the top of the list
            firstResult(offset ?: 0)
            maxResults(100)
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
    private void getListForQuery() {
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
    private void getInstance() {
        Map<String, Object> domainClasses = new HashMap<String, Object>()
        
        // If the id is specified in the url, get it from the parameters
        // Otherwise, it is of type long
        Long rId = null
        if (viewElement.id) {
            rId = (viewElement.id.equalsIgnoreCase("url")) ? params.long("id") : String.toLong(viewElement.id)
        }            
        
        viewElement.allDomainClasses.each { domainClass ->
            Object instance = (rId) ? domainClass.clazz.get(rId) : domainClass.newInstance()
            domainClasses.put(domainClass.name, instance)
        } 
        
        results = domainClasses
    }
}

