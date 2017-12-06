package org.iisg.eca.dynamic

import grails.orm.HibernateCriteriaBuilder

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes

import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.hibernate.criterion.CriteriaSpecification
import org.iisg.eca.domain.EventDateDomain
import org.iisg.eca.domain.EventDomain

/**
 * Queries the database for results based on the information from the <code>DataContainer</code>
 */
class DynamicPageResults {
    static ctx = ServletContextHolder.servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
    static pageInformation = ctx.getBean('pageInformation')

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
     * Returns the list with unfiltered (from the users filter options) results from the database
     * @return A list of results
     */
    List getUnfiltered() {
        getList(false)
    }
    
    /**
     * Returns the result of the given domain class name
     * @param domainClass The name of the domain class to fetch the result of
     * @return The result of the given domain class name
     */
    Object get(String domainClass) {
        Object result = results.find { it.class.simpleName == domainClass }

        if (!result) {
            GrailsDomainClass dc = (GrailsDomainClass) newInstances.get(domainClass)
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
            GrailsDomainClass dc = (GrailsDomainClass) newInstances.get(domainClass)
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
     * If a record with the specified id cannot be found, null is returned
     * This will return whether any null records were returned
     * @return The results contain null records
     */
    boolean containsNullResults() {
        results.contains(null)
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
            results = getList()
        }
        else {                  
            results = getInstance()
        }   
    }
    
    /**
     * Returns a list of the requested data for use with a table
     * @return A list with results
     */
    private List getList(boolean filterAll = true) {
        // The criteria builder will be used to query the database, so delegate it to the closure we just defined
        HibernateCriteriaBuilder criteria = dataContainer.domainClass.clazz.createCriteria()
        criteria.listDistinct {
            // If there are child columns defined, then first set the level to the referencing domain class
            dataContainer.forAllColumnsWithChildren { withChild ->
                createAlias(withChild.name, withChild.name, CriteriaSpecification.LEFT_JOIN)
            }

            // Loop over all the columns and place filters if needed
            dataContainer.getAllColumns().each { c ->
                // Only sort and filter on those that will be displayed anyway
                if (!c.hasColumns() && (!c.constrainedProperty || c.constrainedProperty.display) && !c.hidden && filterAll) {
                    String filter = params["filter_${dataContainer.eid}_${c.name}"]

                    // Place a filter on this column (currently only Strings and booleans) if specified by the user
                    if (filter && !filter.isEmpty()) {
                        if (c.property.type == Boolean || c.property.type == boolean) {
                            if ((filter == "0") || (filter == "1")) {
                                eq(c.fullName, filter.equals("1"))
                            }
                        }
                        else if (c.property.type == String)  {
                            String[] filters = filter.split()
                            filter = "%${filters.join('%')}%"
                            like(c.fullName, filter)
                        }
                        else if (c.property.manyToOne || c.property.oneToOne)  {
                            if (filter?.isLong()) {
                                eq(c.fullName + ".id", new Long(filter))
                            }
                            else if (c.filter) {
                                isNull(c.fullName + ".id")
                            }
                        }
                        else if ((c.property.oneToMany || c.property.manyToMany) && c.filterColumn) {
                            String[] filters = filter.split()
                            filter = "%${filters.join('%')}%"
                            like("${c.fullName}.${c.filterColumn}", filter)
                        }
                    }
                    else if (c.filter) {
                        isNull(c.fullName + ".id")
                    }
                }

                // Now apply the filter from the column description, if there is one
                if (c.eq) {
                    String value = null
                    // Figure out the exact value to filter on, the given property or from the url...
                    if (c.eq.equalsIgnoreCase("url")) {
                        if (params.id) {
                            value = params.id
                        }
                    }
                    else {
                        value = c.eq
                    }

                    // Figure out if we're filtering on a relationship to another domain class or not
                    if (c.property.otherSide instanceof GrailsDomainClassProperty && value) {
                        eq("${c.fullName}.id", value.toLong())
                    }
                    else if (value) {
                        if (c.property.type == Boolean || c.property.type == boolean) {
                            eq(c.fullName, value.equals("1"))
                        }
                        else if (value.equalsIgnoreCase("null")) {
                            isNull(c.fullName)
                        }
                        else {
                            eq(c.fullName, value)
                        }
                    }
                }

                // Also check for empty values
                if (c.notEmpty) {
                    isNotEmpty(c.fullName)
                }

                // Make sure that referenced domain classes also filter events, event dates and soft deletes
                if (c.hasColumns() && pageInformation.date && EventDateDomain.class.isAssignableFrom(c.property.referencedDomainClass.clazz)) {
                    eq("${c.fullName}.date.id", pageInformation.date.id)
                }
                if (c.hasColumns() && pageInformation.date && EventDomain.class.isAssignableFrom(c.property.referencedDomainClass.clazz)) {
                    eq("${c.fullName}.event.id", pageInformation.date.event.id)
                }
                if (c.hasColumns() && c.property.referencedDomainClass.hasProperty('deleted')) {
                    eq("${c.fullName}.deleted", false)
                }
            }
            
            // Sort the columns
            params["sort_${dataContainer.eid}"]?.split(';')?.each { sortInfo ->
                sortInfo = sortInfo?.split(':')
                Column column = dataContainer.getColumnInHierarchy(sortInfo[0]?.trim())
                
                String sort = null
                if (sortInfo.length == 2) {
                    sort = sortInfo[1]?.toLowerCase()?.trim()
                }
                
                if (column && (sort == "asc" || sort == "desc")) {
                    order(column.fullName, sort)
                }
            }
        }
    }

    /**
     * Returns an instance from the database, based on a specified element
     * @return A list with results
     */
    private List getInstance() {
        List<Object> domainClasses = new ArrayList<Object>()
        
        // If the id is specified in the url, get it from the parameters
        // Otherwise, it is of type long
        Long rId = null
        if (dataContainer.id) {
            rId = (dataContainer.id.equalsIgnoreCase("url")) ? params.long("id") : dataContainer.id.toLong()
        }            
        
        // Fetch the instances
        dataContainer.domainClasses.each { domainClass ->
            Object instance = (rId) ? domainClass.clazz.findById(rId) : domainClass.newInstance()
            
            dataContainer.forAllColumns { c ->
                if (c.eq && (c.property.type == Boolean || c.property.type == boolean)) {                    
                    instance = (c.eq.equals("1") == instance[c.name]) ? instance : null         
                }
                else if (c.eq) {
                    instance = (c.eq == instance[c.name]) ? instance : null     
                }               
            }
            
            domainClasses.add(instance)
            dataContainer.forAllColumnsWithChildren { c ->
                if (c.domainClass == domainClass) {
                    instance[c.name]?.each {
                        domainClasses.add(it)
                    }
                }
            }
        }

        // In case of multiples, a new instance could be created, so cache these
        dataContainer.domainClassesOfMultiples.each { domainClass ->
            newInstances.put(domainClass.name, domainClass)
        }
        
        domainClasses
    }
}

