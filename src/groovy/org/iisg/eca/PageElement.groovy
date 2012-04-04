package org.iisg.eca

/**
 * A representation of an element from a dynamic page
 */
class PageElement {
    private Type type
    private Map<String, List> columns
    private List result

    /**
     * The possible element types
     */
    enum Type {
        FORM, TABLE, OVERVIEW
    }

    /**
     * Creates a new instance of a page element
     * @param type The type of this element
     * @param columns A map of all columns requested for this element linked to their domain class
     * @param result A list of all the results from the database for this element
     */
    PageElement(Type type, Map<String, List> columns, List result) {
        this.type = type
        this.columns = columns
        this.result = result
    }

    /**
     * Returns the type of this element
     * @return The element type
     */
    Type getType() {
        type
    }

    /**
     * Returns a map of all the columns requested for this element, linked to their domain class
     * @return A map of all the columns
     */
    Map<String, List> getColumns() {
        columns
    }

    /**
     * A list of all the results from the database for this element
     * @return A list of all the results
     */
    List getResult() {
        result
    }

    /**
     * Returns all domain classes for this element
     * @return A set of domain class names
     */
    Set<String> getDomainClassNames() {
        columns.keySet()
    }

    /**
     * Returns a list of all columns requested for this element by the specified domain class name
     * @param domainClass The domain class name in question
     * @return A list of all columns
     */
    List<String> getColumnsByDomainClassName(String domainClass) {
        columns.get(domainClass)
    }

    /**
     * Returns the name of the domain class belonging to the specified column
     * @param column The name of the column in question
     * @return The name of the domain class
     */
    String getDomainClassByColumn(String column) {
        String domainClass
        domainClassNames.each { dc ->
            if (columns.get(dc).contains(column)) {
               domainClass = dc.toString()
            }
        }
        domainClass
    }

    /**
     * Returns a list of all the column names requested for this element
     * @return A list of all the columns
     */
    List<String> getAllColumns() {
        def all = new ArrayList()
        domainClassNames.each { dc ->
            all += columns.get(dc)
        }
        all
    }
}
