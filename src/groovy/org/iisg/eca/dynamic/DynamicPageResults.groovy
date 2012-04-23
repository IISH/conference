package org.iisg.eca.dynamic

import grails.orm.HibernateCriteriaBuilder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 * Queries the database for results based on the information from the <code>DataContainer</code>
 */
class DynamicPageResults {
    private DataContainer dataContainer
    private GrailsParameterMap params
    private List results
    private Map<String, Object> newInstances
    
    /**
     * Creates a new <code>DynamicPageResults</code> instance
     * for the specified data container element
     * @param dataContainer The container element to fetch the data for
     * @param params The currently requested parameters
     */
    DynamicPageResults(DataContainer dataContainer, GrailsParameterMap params) {
        this.dataContainer = dataContainer
        this.params = params
        this.newInstances = new HashMap<String, Object>()
        getResults()
    }

    /**
     * Returns the container element for which the data was fetched from the database
     * @return The data container element
     */
    DataContainer getDataContainer() {
        dataContainer
    }
    
    /**
     * Returns the list with results from the database
     * In case of a form or overview, the list consists of several different
     * objects with the necessary data
     * In case of a table, the list represents all rows consisting of 
     * only one object or an array of objects per row
     * @return A list of results
     */
    List get() {
        results
    }
    
    /**
     * Returns the result of the given domain class name
     * @param domainClass The name of the domain class to fetch the result of
     * @return The result of the given domain class name
     */
    Object get(String domainClass) {
        Object result = results.find { it.class.simpleName == domainClass }

        if (!result) {
            GrailsDomainClass dc = newInstances.get(domainClass)
            result = dc.newInstance()
            results.add(result)
        }

        result
    }

    /**
     * Returns the result of the given domain class name and id
     * @param domainClass The name of the domain class to fetch the result of
     * @param id The id of the object to fetch
     * @return The result of the given domain class name and id
     */
    Object get(String domainClass, Long id) {
        Object result = null

        if (id) {
            result = results.find { (it.class.simpleName == domainClass) && (it.id == id) }
        }

        if (!result) {
            GrailsDomainClass dc = newInstances.get(domainClass)
            result = dc.newInstance()
            results.add(result)
        }

        result
    }

    /**
     * Returns the results of the given domain class name
     * @param domainClass The name of the domain class to fetch the results of
     * @return The results of the given domain class name
     */
    Object[] getArray(String domainClass) {
        results.findAll { it.class.simpleName == domainClass } as Object[]
    }
    
    /**
     * Returns a model representation of the results
     * @return A map of the results, numbered with an index as key
     */
    Map getModel() {
        Map model = [:]
        results.eachWithIndex { it, i -> model.put(i, it) }
        model
    }
    
    /**
     * Queries the database for the necessary data
     */
    private void getResults() {
        if (dataContainer.type == DataContainer.Type.TABLE) {
            if (dataContainer.query) {
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
     * Returns a list of the requested data for use with a table
     */
    private void getList() {
        HibernateCriteriaBuilder criteria = dataContainer.domainClass.clazz.createCriteria()
        
        results = criteria.list {
            and {
                // Loop over all the columns and place filters if needed
                dataContainer.forAllColumns { c ->
                    if (c.parent instanceof DataContainer) {
                        String filter = params["filter_${dataContainer.eid}_${c.name}"]
                        String sort = params["sort_${dataContainer.eid}_${c.name}"]

                        // Place a filter on this column, if specified by the user
                        if (filter && !filter.isEmpty()) {
                            String[] filters = filter.split()
                            filter = "%${filters.join('%')}%"
                            like(c.name, filter)
                        }

                        // Sort this column, if specified by the user
                        if (sort && !sort.isEmpty()) {
                            order(c.name, sort)
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns a list of the requested data 
     * for use with a table for the specified query
     */
    private void getListForQuery() {
        // TODO
    }

    /**
     * Returns an instance from the database, based on a specified element
     */
    private void getInstance() {
        List<Object> domainClasses = new ArrayList<Object>()
        
        // If the id is specified in the url, get it from the parameters
        // Otherwise, it is of type long
        Long rId = null
        if (dataContainer.id) {
            rId = (dataContainer.id.equalsIgnoreCase("url")) ? params.long("id") : dataContainer.id.toLong()
        }            
        
        // Fetch the instances
        dataContainer.domainClasses.each { domainClass ->
            Object instance = (rId) ? domainClass.clazz.get(rId) : domainClass.newInstance()
            domainClasses.add(instance)
            dataContainer.forAllColumnsWithChildren { c ->
                if (c.domainClass == domainClass) {
                    instance[c.name]?.each { domainClasses.add(it) }
                }
            }
        }

        // In case of multiples, a new instance could be created, so cache these
        dataContainer.domainClassesOfMultiples.each { domainClass ->
            newInstances.put(domainClass.name, domainClass)
        }
        
        results = domainClasses
    }
}

