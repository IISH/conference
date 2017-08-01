package org.iisg.eca.dynamic

import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.validation.ConstrainedProperty

/**
 * Builds a html table based on the given element
 */
class OverviewBuilder extends ElementBuilder {

    /**
     * Creates a new <code>TableBuilder</code> for use with container elements
     * @param builder The markup builder
     */
    OverviewBuilder(MarkupBuilder builder) {
        this.builder = builder
    }

    /**
     * Builds a html table based on the information of the given element
     * @param element The container element which represents a table
     */
    void build(DataContainer element) {
        builder.ol(class: "property-list ${element.classNames}") {
            buildColumns(element.columns)
        }
    }

    /**
     * Builds the overview columns based on the information of the given columns
     * @param columns The list of columns to be represented in the overview
     */
    private void buildColumns(List<Column> columns) {
        columns.each { c ->
            GrailsDomainClassProperty p = c.property
            ConstrainedProperty cp = c.constrainedProperty

            boolean display = (cp ? cp.display : true)

            // If the column contains other columns, build them instead
            if (c.hasElements()) {
                buildColumns(c.columns)
            }
            // If the column is actually a collection, then list all of the items in the collection
            else if (display && Collection.class.isAssignableFrom(c.property.type)) {
                String inVar = "\${${RESULTS}.get(${c.root.eid}).get('${c.domainClass.name}')['${c.name}']}"
                builder.li {
                    builder.span(id: "${c.name}-label", class: "property-label") {
                        builder."eca:fallbackMessage"(code: getCode(c.property), fbCode: getFbCode(c.property))
                    }
                    builder.ul(class: "property-value", "arial-labelledby": "${c.name}-label") {
                        builder."g:each"(in: inVar, var: "element", status: "i") {
                            builder."g:if"(test: "\${!element.domainClass.hasProperty('deleted') || !element.deleted}") {
                                builder.li("\${element.encodeAsHTML()}")
                            }
                        }
                    }
                }
            }
            // Otherwise just print the column
            else if (display && !c.hidden) {
                builder.li {
                    if (c.name == "id") {
                        builder.span(id: "id-label", class: "property-label", "#")
                    } else {
                        builder.span(id: "${c.name}-label", class: "property-label") {
                            builder."eca:fallbackMessage"(code: getCode(c.property), fbCode: getFbCode(c.property))
                        }
                    }

                    String value = "\${${RESULTS}.get(${c.root.eid}).get('${c.domainClass.name}')['${c.name}']}"
                    builder.span(class: "property-value", "arial-labelledby": "${c.name}-label") {
                        if (c.property.type == Boolean || c.property.type == boolean) {
                            builder."g:checkBox"(name: c.name, value: value, disabled: true)
                        } else if (c.property.type == Date || c.property.type == java.sql.Date || c.property.type == java.sql.Time || c.property.type == Calendar) {
                            builder."g:formatDate"(date: value)
                        } else {
                            builder."eca:formatText"(text: value)
                        }
                    }
                }
            }
        }
    }
}
