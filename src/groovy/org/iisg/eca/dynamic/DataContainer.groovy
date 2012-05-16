package org.iisg.eca.dynamic

import org.codehaus.groovy.grails.commons.GrailsDomainClass

/**
 * A representation of an element from a dynamic page
 */
class DataContainer extends ContainerElement {
    private static final String DOMAIN_PACKAGE = "org.iisg.eca"

    private int eid
    private Type type
    private GrailsDomainClass domainClass    
    private String id
    private String query
    private boolean index
    private String action
    
    /**
     * The possible data element types
     */
    enum Type {
        FORM, TABLE, OVERVIEW
    }
    
    /**
     * Creates a new instance of a data container element
     * @param eid The id of this element
     * @param name The name of this element
     * @param type The type of this data container
     * @param domainClass The main domain class for this container
     * @param elements A list of elements contained by this element
     */
    DataContainer(int eid, String name, Type type, GrailsDomainClass domainClass, List<Element> elements) {
        super(name, elements)
        this.eid = eid
        this.type = type
        this.domainClass = domainClass
        this.id = null
        this.query = null
        this.index = false
        this.action = 'show'
    }    
    
    /**
     * Returns the id of this element
     * @return The element id
     */
    int getEid() {
        eid
    }

    /**
     * Returns the type of this element
     * @return The element type
     */
    Type getType() {
        type
    }

    /**
     * Returns the main domain class of this container
     * @return The main domain class
     */
    GrailsDomainClass getDomainClass() {
        domainClass
    }
    
    /**
     * Returns all of the domain classes of all columns it contains
     * @returns A set of domain classes to query the data that is necessary
     */
    Set<GrailsDomainClass> getAllDomainClasses() {
        Set<GrailsDomainClass> domainClasses = new HashSet<GrailsDomainClass>()
        forAllColumns { c -> 
            domainClasses.add(c.domainClass)
        }
        domainClasses
    }

    /**
     * Returns all of the domain classes of only those columns directly contained by this container
     * @returns A set of domain classes to query the data that is necessary
     */
    Set<GrailsDomainClass> getDomainClasses() {
        Set<GrailsDomainClass> domainClasses = new HashSet<GrailsDomainClass>()
        columns.each { c ->
            domainClasses.add(c.domainClass)
        }
        domainClasses
    }

    /**
     * Returns all domain classes of only those columns which contain other columns
     * that can be created/edited multiple times
     * @return A set of domain classes to query the data that is necessary
     */
    Set<GrailsDomainClass> getDomainClassesOfMultiples() {
        Set<GrailsDomainClass> domainClasses = new HashSet<GrailsDomainClass>()
        forAllColumnsWithChildren { c ->
            c.columns.each { domainClasses.add(it.domainClass) }
        }
        domainClasses
    }
    
    /**
     * Returns all of the columns for a particular domain class
     * @param domainClass The domain class to return all columns from, contained by this element 
     * @returns A set of columns that have the given domain class
     */
    Set<Column> getAllColumnsForDomainClass(GrailsDomainClass domainClass) {
        Set<Column> columns = new HashSet<Column>()
        forAllColumns { c -> 
            if (c.domainClass == domainClass) {
                columns.add(c)
            }
        }
        columns
    }
    
    /**
     * Returns the id of the data represented, either of type 'long', or 'url' to indicate an id in the URL
     * @returns The id of the element represented
     */
    String getId() {
        id
    }
    
    /**
     * Sets the id of the data represented in this container
     * @param id The id of the element represented, either of type 'long', or 'url' to indicate an id in the URL 
     */
    void setId(String id) {
        if (!id || id.isEmpty() || !(id.equalsIgnoreCase('url') || id.isLong())) {
            this.id = null  
        }
        else {
            this.id = id
        }
    }
    
    /**
     * Returns the query used to query the database if one is specified
     * @returns The query string or null if not specified
     */
    String getQuery() {
        query
    }
    
    /**
     * Sets the query used to query the database
     * @param query The query string
     */
    void setQuery(String query) {
        if (!query || query.isEmpty()) {
            this.query = null     
        }
        else {
            this.query = query
        }  
    }

    /**
     * Indicates whether this data container simply shows the index value
     * @returns Whether this data container shows the index value
     */
    boolean isIndex() {
        index
    }

    /**
     * Sets whether this data container simply shows the index value
     * @param multiple Whether this data container should show the index value
     */
    void setIndex(boolean index) {
        this.index = index
    }

    /**
     * Returns the action to go to, when a column is clicked
     * @returns The action to go to, when a column is clicked
     */
    String getAction() {
        action
    }

    /**
     * Sets the action to go to, when a column is clicked
     * @param action The name of the action to go to, when a column is clicked
     */
    void setAction(String action) {
        if (!action || action.isEmpty()) {
            this.action = 'show'
        }
        else {
            this.action = action
        }
    }
}
