package org.iisg.eca

import grails.orm.HibernateCriteriaBuilder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 * Queries the database for results based on the information from the <code>DataContainer</code>
 */
class DynamicPageResults {
    private DataContainer dataContainer
    private GrailsParameterMap params
    private def results
    private Map<String, Object> newInstances
    
    DynamicPageResults(DataContainer dataContainer, GrailsParameterMap params) {
        this.dataContainer = dataContainer
        this.params = params
        this.newInstances = new HashMap<String, Object>()
        getResults()
    }
    
    def get() {
        results
    }
    
    def get(String domainClass) {
        results.find { it.class.simpleName == domainClass }
    }

    def get(String domainClass, Long id) {
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

    def getArray(String domainClass) {
        results.findAll { it.class.simpleName == domainClass }

        /*if (result.length == 0) {
            /*String[] multiple = domainClass.split('_')
            String domainClassToClone = domainClass
            if (multiple.length > 1) {
                domainClassToClone = multiple[0..multiple.length-2].join('_')
            }
            results. .get(domainClass, results.get(domainClassToClone).class.newInstance())
        }
        else {
            return result
        }       */
    }

    void remove(String domainClass) {
        results.remove(domainClass)
    }
    
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
     * Returns a list of all data in the database for the specified main domain class
     * @param mainDomainClass The domain class to query on for a list of data
     * @param columns A list of the columns, from which some may need to be filtered or sorted
     * @param element The part of the xml describing the element
     * @param params The parameters of the current request
     * @return A list of all data in the database for the specified main domain class
     */
    private void getList() {
        HibernateCriteriaBuilder criteria = dataContainer.domainClass.clazz.createCriteria()
        String offset = params.offset?.toString()
        
        results = criteria.list {
            /*dataContainer.forAllColumnsWithChildren { c ->
                String path = c.path.grep { it instanceof Column }.join(".")
                ${path} { }
            }    */

           /* projections {
                dataContainer.forAllColumnsWithChildren { c ->
                    if (c.property.manyToMany || c.property.oneToMany) {
                        String path = c.path.grep { it instanceof Column }.join(".")
                        groupProperty(path)
                    }
                    /*else if (c.property.oneToOne || c.property.manyToOne) {
                        String path = "${c.path.grep { it instanceof Column }.join(".")}.id"
                        property(path)
                    }
                }
            }     */
            
            and {
                // Loop over all the columns and place filters if needed
                dataContainer.forAllColumns { c ->
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

            // For pagination, find out the maximum number of results and the offset.
            // If not specified, return at most 100 results starting from the top of the list
            firstResult(offset?.toInteger() ?: 0)
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
        List<Object> domainClasses = new ArrayList<Object>()
        
        // If the id is specified in the url, get it from the parameters
        // Otherwise, it is of type long
        Long rId = null
        if (dataContainer.id) {
            rId = (dataContainer.id.equalsIgnoreCase("url")) ? params.long("id") : dataContainer.id.toLong()
        }            
        
        dataContainer.domainClasses.each { domainClass ->
            Object instance = (rId) ? domainClass.clazz.get(rId) : domainClass.newInstance()
            domainClasses.add(instance)
            dataContainer.forAllColumnsWithChildren { c ->
                if (c.domainClass == domainClass) {
                    instance[c.name]?.each { domainClasses.add(it) }
                }
            }
        }

        dataContainer.domainClassesOfMultiples.each { domainClass ->
            newInstances.put(domainClass.name, domainClass)
        }
        
        results = domainClasses
    }
}

