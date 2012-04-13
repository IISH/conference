package org.iisg.eca

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsApplication

import groovy.lang.Closure

/**
 * A representation of an element from a dynamic page
 */
class ViewElement extends PageElement {
    private static final String DOMAIN_PACKAGE = "org.iisg.eca"

    private GrailsDomainClass domainClass
    private String id = null
    private String query = null    
    
    /**
     * Creates a new instance of a page element
     * @param type The type of this element
     * @param columns A map of all columns requested for this element linked to their domain class
     * @param result A list of all the results from the database for this element
     */
    ViewElement(int eid, PageElement.Type type, GrailsDomainClass domainClass, List<Element> elements) {
        super(eid, type, elements)
        this.domainClass = domainClass
    }
    
    void forAllColumns(Closure callable) {
        columns.each { c -> 
            callable(c)            
            forAllColumnsFrom(c, callable)
        }
    }
    
    static void forAllColumnsFrom(Column column, Closure callable) {
        column.children.each { c -> 
            callable(c)            
            forAllColumnsFrom(c, callable)
        }
    }
    
    void forAllColumnsWithChildren(Closure callable) {
        columns.each { c -> 
            if (c.hasChildren()) {
                callable(c)            
                forAllColumnsWithChildrenFrom(c, callable)
            }
        }
    }
    
    static void forAllColumnsWithChildrenFrom(Column column, Closure callable) {
        column.children.each { c -> 
            if (c.hasChildren()) {
                callable(c)            
                forAllColumnsWithChildrenFrom(c, callable)
            }
        }
    }   
    
    Set<GrailsDomainClass> getAllDomainClasses() {
        Set<GrailsDomainClass> domainClasses = new HashSet<GrailsDomainClass>()
        forAllColumns { c -> 
            domainClasses.add(c.domainClass)
        }
        domainClasses
    }
    
    Set<Column> getAllColumnsForDomainClass(GrailsDomainClass domainClass) {
        Set<Column> columns = new HashSet<Column>()
        forAllColumns { c -> 
            if (c.domainClass == domainClass) {
                columns.add(c)
            }
        }
        columns
    }
    
    String getId() {
        id
    }
    
    void setId(String id) {
        if (!id || id.isEmpty() || !(id.equalsIgnoreCase('url') || id.isLong())) {
            this.id = null  
        }
        else {
            this.id = id
        }
    }
    
    String getQuery() {
        query
    }
    
    void setQuery(String query) {
        if (!query || query.isEmpty()) {
            this.query = null     
        }
        else {
            this.query = query
        }  
    }
}
