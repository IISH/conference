package org.iisg.eca.dynamic

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.validation.ConstrainedProperty

/**
 * An element representing a column in the database / property of a domain class
 */
class Column extends ContainerElement {
    private GrailsDomainClass domainClass
    private boolean id
    private boolean readOnly
    private boolean multiple
    private boolean hidden
    private boolean notEmpty
    private boolean filter
    private boolean hideFilter
    private boolean hideSorting
    private String interactive
    private String textarea
    private String eq
    private String filterColumn

    /**
     * Creates a new <code>Column</code> element
     * @param name The name given in the domain class it is referring to
     * @param domainClass The domain class that is referred to
     * @param elements A column can only contain other columns; they need a relationship
     */
    Column(String name, GrailsDomainClass domainClass, List<Column> elements) {
        super(name, elements)
        this.domainClass = domainClass
        this.id = false
        this.readOnly = false
        this.multiple = false
        this.hidden = false
        this.notEmpty = false
        this.filter = false
        this.hideFilter = false
        this.textarea = null
        this.eq = null
        this.filterColumn = null
    }

    /**
     * Returns the domain class of this column
     * @returns The domain class of this column
     */ 
    GrailsDomainClass getDomainClass() {
        domainClass
    }

    /**
     * Returns the property object of this column
     * @returns The <code>GrailsDomainClassProperty</code> of this column
     */ 
    GrailsDomainClassProperty getProperty() {
        domainClass?.getPropertyByName(name)
    }

    /**
     * Returns the constrained property object of this column
     * @returns The <code>ConstrainedProperty</code> of this column
     */ 
    ConstrainedProperty getConstrainedProperty() {
        domainClass?.getConstrainedProperties()?.get(name)
    }
    
    boolean isId() {
        id
    }
    
    /**
     * Indicates whether it is allowed to change this value in a form
     * @returns Whether this column is read only
     */
    boolean isReadOnly() {
        readOnly
    }

    /**
     * Indicates whether this column can be created multiple times in a form
     * @returns Whether this column can have multiple values created at once
     */
    boolean isMultiple() {
        multiple
    }

    /**
     * Indicates whether this column should be hidden
     * @returns Whether this column should be hidden
     */
    boolean isHidden() {
        hidden
    }
    
    void setId(boolean id) {
        this.id = id
    }

    /**
     * Sets whether it is allowed to change this value in a form
     * @param readOnly Whether this column is read only
     */
    void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly
    }

    /**
     * Sets whether this column can be created multiple times in a form
     * @param multiple Whether this column can have multiple values created at once
     */
    void setMultiple(boolean multiple) {
        this.multiple = multiple
    }

    /**
     * Sets whether this column should be hidden or not
     * @param hidden Whether this column should be hidden or not
     */
    void setHidden(boolean hidden) {
        this.hidden = hidden
    }
    
    void setNotEmpty(boolean notEmpty) {
        this.notEmpty = notEmpty
    }
    
    boolean getNotEmpty() {
        notEmpty
    }

    void setFilter(boolean filter) {
        this.filter = filter
    }

    boolean getFilter() {
        filter
    }

    void setHideFilter(boolean hideFilter) {
        this.hideFilter = hideFilter
    }

    boolean getHideFilter() {
        hideFilter
    }

    void setHideSorting(boolean hideSorting) {
        this.hideSorting = hideSorting
    }

    boolean getHideSorting() {
        hideSorting
    }

    void setInteractive(String interactive) {
        if (interactive && (interactive.trim().length() > 0)) {
            this.interactive = interactive.trim()
        }
    }
    
    String getInteractive() {
        interactive
    }
    
    /**
     * Indicates whether this column should be a textarea if not null and with what size
     * @returns Either null, 'normal' or 'large'
     */
    String getTextarea() {
        textarea
    }

    /**
     * Sets whether this column should be displayed as a textarea in a form either normal or large sized
     * @param textarea Either 'normal' or 'large'
     */
    void setTextarea(String textarea) {
        if (textarea == 'normal' || textarea == 'large') {
            this.textarea = textarea
        }
    }

    /**
     * Indicates whether all results should have the same value
     * @returns Which value all the results need to have for this column
     */
    String getEq() {
        eq
    }

    /**
     * Sets whether all of this columns results should have this value
     * @param eq What the column should have for value
     */
    void setEq(String eq) {
        if (eq && (eq.trim().length() > 0)) {
            this.eq = eq.trim()
        }
    }

    String getFilterColumn() {
        filterColumn
    }

    void setFilterColumn(String filterColumn) {
        if (filterColumn && (filterColumn.trim().length() > 0)) {
            this.filterColumn = filterColumn.trim()
        }
    }

    /**
    * Returns all of the elements on the way to the root
    * @returns A list of all the elements on the way to the root
    */
    List<Column> getColumnPath() {
        List<Element> path = this.path
        path.grep { it instanceof Column } as List<Column>
    }

    String getFullName() {
        getColumnPath().collect { it.name }.join('.')
    }

    /**
     * Returns whether this column can be shown to the user
     * @return Whether it can be shown
     */
    boolean canBeShown() {
        (!hasColumns() && (!constrainedProperty || constrainedProperty.display))
    }

    Column getParentColumn() {
        (parent instanceof Column) ? parent : null
    }
}