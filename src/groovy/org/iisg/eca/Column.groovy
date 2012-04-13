package org.iisg.eca

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.validation.ConstrainedProperty

class Column extends Element {
    private GrailsDomainClass domainClass
    private boolean readOnly
    private boolean multiple

    private Column parent
    private List<Column> children

    Column(String name, GrailsDomainClass domainClass, List<Column> children) {
        super(name, Element.ElementType.COLUMN)

        this.domainClass = domainClass

        this.parent = null
        this.readOnly = false
        this.multiple = false

        setChildren(children)
    }

    GrailsDomainClass getDomainClass() {
        domainClass
    }

    GrailsDomainClassProperty getProperty() {
        domainClass.getPropertyByName(name)
    }

    ConstrainedProperty getConstrainedProperty() {
        domainClass.getConstrainedProperties().get(name)
    }

    boolean isReadOnly() {
        readOnly
    }

    boolean isMultiple() {
        multiple
    }

    void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly
    }

    void setMultiple(boolean multiple) {
        this.multiple = multiple
    }

    List<Column> getChildren() {
        children
    }

    Column get(String name) {
        children.find { it.name.equalsIgnoreCase(name) }
    }

    void setChildren(List<Column> children) {
        this.children = children

        if (!children.isEmpty()) {
            GrailsDomainClass domain = property.referencedDomainClass
            children.each { c ->
                if (domain && c.domainClass == domainClass) {
                    c.domainClass = domain
                }
                c.parent = this
            }
        }
    }

    void addChild(Column child) {
        children.add(child)
        child.parent = this

        GrailsDomainClass domain = property.getReferencedDomainClass()
        if (domain && child.domainClass == domainClass) {
            child.domainClass = domain
        }
    }

    boolean hasChildren() {
        !children.isEmpty()
    }

    Column getParent() {
        parent
    }

    void setParent(Column parent) {
        this.parent = parent
    }

    @Override
    void setPageElement(PageElement pageElement) {
        super.setPageElement(pageElement)
        children*.pageElement = pageElement
    }

    List<Column> getPath() {
        List<Column> columns = new ArrayList<Column>()
        Column current = this

        while (current) {
            columns.add(1, current)
            current = current.parent
        }

        columns
    }
}