package org.iisg.eca

import groovy.xml.MarkupBuilder
import grails.persistence.Event
import org.codehaus.groovy.grails.scaffolding.DomainClassPropertyComparator
import java.lang.reflect.Type
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty

/**
 * A tag library responsible for parsing the xml content of a dynamic page
 */
class DynamicPageTagLib {
    private static excludedProps = Event.allEvents.toList() << 'version' << 'dateCreated' << 'lastUpdated'

    static namespace = "element"

    def grailsApplication
    def groovyPagesTemplateEngine

    /**
     * The type of elements expected
     */
    enum ElementType {
        TABLE, FORM, OVERVIEW, BUTTONS
    }

    /**
     * Creates a new form, based on all the columns and buttons specified in the body
     * @param domain The main domain class to edit or save information to
     * @param id The id to query the database in order to fill out this form, 'url' is specified in the url
     */
    def form = { attrs, body ->
        def writer = new StringWriter()
        def builder = new MarkupBuilder(writer)
        builder.doubleQuotes = true

        pageScope.eid = (pageScope.eid) ? pageScope.eid++ : 0
        pageScope.parent = ElementType.FORM
        pageScope.builder = builder
        pageScope.domainClass = grailsApplication.getDomainClass("org.iisg.eca.${attrs.domain}")
        pageScope.renderEditor = new RenderEditor(pageScope.domainClass, pageScope.builder, pageScope.eid)
        pageScope.props = pageScope.domainClass.properties.findAll {
            pageScope.domainClass.properties*.name.contains(it.name) && !excludedProps.contains(it.name)
        }
        Collections.sort(pageScope.props, new DomainClassPropertyComparator(pageScope.domainClass))

        builder.form(method: "post", action: "#") {
            builder.input(type: "hidden", name: "id", value: "\${page.elements.get(${pageScope.eid}).getResultByDomainClassName('${pageScope.domainClass.propertyName}').id}")
            builder.input(type: "hidden", name: "eid", value: pageScope.eid)
            builder.fieldset(class: "form") {
                pageScope.type = 3
                body()
            }
            builder.fieldset(class: "buttons") {
                pageScope.type = 4
                body()
            }
        }

        out << writer.toString()
    }

    /**
     * Creates a new table, based on all the columns specified in the body
     * @param domain The main domain class to query for data to fill the table
     * @param query The query to obtain data from the database for this table
     */
    def table = { attrs, body ->
        def writer = new StringWriter()
        def builder = new MarkupBuilder(writer)
        builder.doubleQuotes = true

        pageScope.eid = (pageScope.eid) ? pageScope.eid++ : 0
        pageScope.parent = ElementType.TABLE
        pageScope.children = new ArrayList()
        pageScope.builder = builder
        pageScope.domainClass = grailsApplication.getDomainClass("org.iisg.eca.${attrs.domain}")
        pageScope.props = pageScope.domainClass.properties.findAll {
            pageScope.domainClass.properties*.name.contains(it.name) && !excludedProps.contains(it.name)
        }
        Collections.sort(pageScope.props, new DomainClassPropertyComparator(pageScope.domainClass))

        builder.div(class: "tbl_container") {
            builder.form(action: "#", type: "get") {
                builder.input(type: "hidden", name: "eid", value: pageScope.eid)
                builder.div(class: "tbl_toolbar") {
                    builder.input(type: "submit", name: "format", value: "Filter", class: "tbl_filter")
                    builder.input(type: "submit", name: "format", value: "CSV", class: "tbl_csv")
                    builder.input(type: "submit", name: "format", value: "XLS", class: "tbl_xls")
                    builder.input(type: "submit", name: "format", value: "XML", class: "tbl_xml")
                    builder.input(type: "text", name: "name", placeholder: "Name of exported file")
                    builder.input(type: "text", name: "sep", placeholder: "Seperator in CSV file", length: "3")
                }
                builder.table {
                    builder.thead {
                        builder.tr {
                            pageScope.type = 1
                            body()
                        }
                        builder.tr {
                            pageScope.type = 2
                            body()
                        }
                    }
                    builder.tbody {
                        builder."g:each"(in: "\${page.elements.get(${pageScope.eid}).getResultByDomainClassName('${pageScope.domainClass.propertyName}')}", var: "row") {
                            builder.tr {
                                pageScope.children.eachWithIndex { p, i ->
                                    if (i == 0) {
                                        builder.td {
                                            builder.a(href: "${attrs.action}/\${row.id}", "\${row['${p.name}']}")
                                        }
                                    }
                                    else {
                                        parseValue(p.name, p.type, "\${row['${p.name}']}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        out << writer.toString()
    }

    /**
     * Creates a new overview, based on all the columns specified in the body
     * @param domain The main domain class to query for data to fill out the overview
     * @param id The id to query the database in order to fill out this overview, 'url' is specified in the url
     */
    def overview = { attrs, body ->
        def writer = new StringWriter()
        def builder = new MarkupBuilder(writer)
        builder.doubleQuotes = true

        pageScope.eid = (pageScope.eid) ? pageScope.eid++ : 0
        pageScope.parent = ElementType.OVERVIEW
        pageScope.builder = builder
        pageScope.domainClass = grailsApplication.getDomainClass("org.iisg.eca.${attrs.domain}")
        pageScope.props = pageScope.domainClass.properties.findAll {
            pageScope.domainClass.properties*.name.contains(it.name) && !excludedProps.contains(it.name)
        }
        Collections.sort(pageScope.props, new DomainClassPropertyComparator(pageScope.domainClass))

        builder.ol(class: "property-list") {
            body()
        }

        out << writer.toString()
    }

    /**
     * Creates a container to hold buttons
     */
    def buttons = { attrs, body ->
        def writer = new StringWriter()
        def builder = new MarkupBuilder(writer)
        builder.doubleQuotes = true
        pageScope.builder = builder

        builder.div(class: 'buttons') {
            body()
        }

        out << writer.toString()
    }

    /**
     * Specifies the column to display
     * @param name The name of the column as known by the domain class
     * @param domain If the data is to come from another domain class, it is specified here
     */
    def column = { attrs ->
        def p, cp

        if (attrs.domain) {
            def domain = grailsApplication.getDomainClass("org.iisg.eca.${attrs.domain}")
            pageScope.columnRenderEditor = new RenderEditor(domain, pageScope.builder, pageScope.eid)
            p = domain.properties.findAll {
                domain.properties*.name.contains(it.name) && !excludedProps.contains(it.name)
            }.grep { it.name == attrs.name }[0]
            cp = domain.constrainedProperties[attrs.name]
        }
        else {
            p = pageScope.props.grep { it.name == attrs.name }[0]
            cp = pageScope.domainClass.constrainedProperties[attrs.name]
        }

        def display = (cp ? cp.display : true)
        def required = (cp ? !(cp.propertyType in [boolean, Boolean]) && !cp.nullable && (cp.propertyType != String || !cp.blank) : false)

        if (!display) {
            return
        }

        switch (pageScope.parent) {
            case ElementType.FORM:
                columnForForm(attrs, p, cp, required)
                break
            case ElementType.TABLE:
                columnForTable(attrs, p, cp, required)
                break
            case ElementType.OVERVIEW:
                columnForOverview(attrs, p, cp, required)
                break
        }
    }

    /**
     * Creates a new button
     * @param type The type of button: 'save' for forms, 'back' and 'cancel' for back buttons
     */
    def button = { attrs ->
        if (pageScope.type != 3) {
            def builder = pageScope.builder

            if (attrs.type.equalsIgnoreCase("save") || attrs.type.equalsIgnoreCase("back") || attrs.type.equalsIgnoreCase("cancel")) {
                def map = new HashMap()
                map.put("name", "btn_${attrs.type}")
                map.put("class", "btn_${attrs.type}")
                map.put("value", "\${message(code: 'default.button.${attrs.type}.label')}")

                if (attrs.type.equalsIgnoreCase("save")) {
                    map.put("type", "submit")
                }
                else {
                    map.put("type", "button")
                }

                builder.input(map)
            }
            else {
                builder.a(href: "../..${attrs.url}/${params.id}", name: "btn_${attrs.type}", class: "btn_${attrs.type}") {
                    builder."g:message"(code: "default.button.${attrs.type}.label")
                }
            }
        }
    }

    /**
     * Builds the column in case it belongs to a form
     * @param attrs The attributes for this column
     * @param p The properties for this column as stated by the domain class it belongs to
     * @param cp The constrained properties for this column as stated by the domain class it belongs to
     * @param required Whether this column is required or not
     */
    private columnForForm(attrs, p, cp, required) {
        def builder = pageScope.builder
        
        if (pageScope.type == 3) {
            // TODO: fix results array
            builder.div(class: "fieldcontain \${hasErrors(bean: page.elements.get(${pageScope.eid}).getResultByDomainClassName('${pageScope.domainClass.propertyName}'), field: '${attrs.name}', 'error')} ${required ? 'required' : ''}") {
                if (attrs.name.equalsIgnoreCase("id")) {
                    builder.label(for: attrs.name, "#")
                }
                else {
                    builder.label(for: attrs.name) {
                        builder."g:message"(code: getCode(p))

                        if (required && !attrs.readonly?.equalsIgnoreCase("true") && !attrs.id) {
                            builder.span(class: "required-indicator", "*")
                        }
                    }
                }

                if (attrs.name.equalsIgnoreCase("id") || attrs.readonly?.equalsIgnoreCase("true")) {
                    if (attrs.domain) {
                        builder.span("\${page.elements.get(${pageScope.eid}).getResultByDomainClassName('${attrs.domain}')['${attrs.name}'].encodeAsHTML()}")
                    }
                    else {
                        builder.span("\${page.elements.get(${pageScope.eid}).getResultByDomainClassName('${pageScope.domainClass.propertyName}')['${attrs.name}'].encodeAsHTML()}")
                    }
                }
                else if (attrs.id && (p.manyToOne || p.oneToOne || p.oneToMany || p.manyToMany)) {
                    if (attrs.id.equalsIgnoreCase("url")) {
                        builder.span("\${${p.type.name}.get(params.long('id')).toString().encodeAsHTML()}")
                        builder.input(type: "hidden", name: "${p.name}.id", value: "\${params.long('id')}")
                    }
                    else {
                        builder.span("\${${p.type}.get(${attrs.id}).toString().encodeAsHTML()}")
                        builder.input(type: "hidden", name: "${p.name}.id", value: attrs.id)
                    }
                }
                else {
                    if (attrs.domain) {
                        pageScope.columnRenderEditor.render(attrs.name)
                    }
                    else {
                        pageScope.renderEditor.render(attrs.name)
                    }
                }
            }
        }
    }

    /**
     * Builds the column in case it belongs to a table
     * @param attrs The attributes for this column
     * @param p The properties for this column as stated by the domain class it belongs to
     * @param cp The constrained properties for this column as stated by the domain class it belongs to
     * @param required Whether this column is required or not
     */
    private columnForTable(attrs, p, cp, required) {
        def builder = pageScope.builder
        
        if (pageScope.type == 1) {
            def domainClass = pageScope.domainClass
            if (attrs.domain) {
                domainClass = grailsApplication.getDomainClass("org.iisg.eca.${attrs.domain}")
            }
            
            pageScope.children << domainClass.getPropertyByName(attrs.name)

            def sorted = "\${(params.sort${pageScope.eid}_${attrs.name}) ? 'sorted' : ''}"
            def order = "\${(params.sort${pageScope.eid}_${attrs.name} == 'asc') ? 'asc' : 'desc'}"

            builder.th(class: "sortable ${sorted} ${order}") {
                builder.a(href: "#") {
                    builder."g:message"(code: getCode(p))
                }
            }
        }
        else {
            builder.th(class: "filter") {
                builder.input(type: "text", name: "filter${pageScope.eid}_${attrs.name}", value: "\${params.filter${pageScope.eid}_${attrs.name}}")
            }
        } 
    }  

    /**
     * Builds the column in case it belongs to an overview
     * @param attrs The attributes for this column
     * @param p The properties for this column as stated by the domain class it belongs to
     * @param cp The constrained properties for this column as stated by the domain class it belongs to
     * @param required Whether this column is required or not
     */
    private columnForOverview(attrs, p, cp, required) {
        def builder = pageScope.builder
        
        if (Collection.class.isAssignableFrom(p.type)) {
            def inVar = "\${page.elements.get(${pageScope.eid}).getResultByDomainClassName('${pageScope.domainClass.propertyName}')['${attrs.name}']}"
            if (attrs.domain) {
                inVar = "\${page.elements.get(${pageScope.eid}).getResultByDomainClassName('${attrs.domain}')['${attrs.name}']}"
            }

            builder."g:each"(in: inVar, var: "element", status: "i") {
                builder.li(class: "fieldcontain") {
                    builder."g:if"(test: "\${i == 0}") {
                        builder.span(id: "${p.name}-label", class: "property-label") {
                            builder."g:message"(code: getCode(p))
                        }
                        builder.span(class: "property-value", "arial-labelledby": "${p.name}-label", "\${element.encodeAsHTML()}")
                    }
                    builder."g:else" {
                        builder.span(class: "property-value", "arial-labelledby": "${p.name}-label", "\${element.encodeAsHTML()}")
                    }
                }
            }
        }
        else {
            builder.li(class: "fieldcontain") {
                if (attrs.name.equalsIgnoreCase("id")) {
                    builder.span(id: "id-label", class: "property-label", "#")
                }
                else {
                    builder.span(id: "${p.name}-label", class: "property-label") {
                        builder."g:message"(code: getCode(p))
                    }
                }

                if (attrs.domain) {
                    parseValue(attrs.name, p.type, "\${page.elements.get(${pageScope.eid}).getResultByDomainClassName('${attrs.domain}')['${attrs.name}']}")
                }
                else {
                    parseValue(attrs.name, p.type, "\${page.elements.get(${pageScope.eid}).getResultByDomainClassName('${pageScope.domainClass.propertyName}')['${attrs.name}']}")
                }
            }
        }
    }

    /**
     * Parses the value of the specified column based on its type
     * @param columnName The name of the column in question
     * @param type The type of the column
     * @param value The value to parse
     * @return The parsed value
     */
    private parseValue(columnName, type, value) {
        if (type == Boolean || type == boolean) {
            pageScope.builder.span(class: "property-value", "arial-labelledby": "${columnName}-label") {
                pageScope.builder."g:checkBox"(name: columnName, value: value, disabled: true)
            }
        }
        else if (type == Date || type == java.sql.Date || type == java.sql.Time || type == Calendar) {
            pageScope.builder.span(class: "property-value", "arial-labelledby": "${columnName}-label") {
                pageScope.builder."g:formatDate"(date: value)
            }
        }
        else {
            pageScope.builder.span(class: "property-value", "arial-labelledby": "${columnName}-label", value)
        } 
    }

    private getCode(GrailsDomainClassProperty property) {
        if (property.name.equalsIgnoreCase("enabled") || property.name.equalsIgnoreCase("deleted")) {
            return "default.${property.name.toLowerCase()}.label"
        }
        else if (property.manyToOne || property.oneToOne || property.oneToMany || property.manyToMany) {
            return "${property.type.simpleName.toLowerCase()}.multiple.label"
        }
        else {
            return "${property.domainClass.propertyName.toLowerCase()}.${property.name.toLowerCase()}.label"
        }
    }
}
