package org.iisg.eca.dynamic

import groovy.xml.MarkupBuilder

import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty

/**
 * Builds a html page based on the elements
 */
class PageBuilder {
    private static final String RESULTS = "results"
    
    private List<ContainerElement> pageElements  
    private StringWriter writer
    private MarkupBuilder builder
    private RenderEditor renderEditor
    
    /**
     * Creates a new <code>PageBuilder</code> for the list of container elements
     * @param pageElements A list of all elements that have to be present on the page
     */
    PageBuilder(List<ContainerElement> pageElements) {
        this.pageElements = pageElements
        this.writer = new StringWriter()
        this.builder = new MarkupBuilder(writer)
        this.builder.doubleQuotes = true
        this.renderEditor = new RenderEditor(builder, RESULTS)
    }
    
    /**
     * Builds the page and returns the resulting html mixed with GSP tags,
     * ready to be rendered with the data from the database
     * @returns A GSP template
     */
    String buildPage() {
        pageElements.each { element -> 
            if (element instanceof DataContainer) {
                switch(element.type) {
                    case DataContainer.Type.FORM:
                        buildForm(element)
                        break
                    case DataContainer.Type.TABLE:
                        buildTable(element)
                        break
                    case DataContainer.Type.OVERVIEW:
                        buildOverview(element)
                } 
            }
            else {
                buildContainer(element) 
            }
        }
        writer.toString()
    }
    
    /**
     * Builds a html form based on the information of the given element
     * @param element The container element which represents a form
     */
    private void buildForm(DataContainer element) {
        // First build a message block for any errors made by the user
        builder."g:hasErrors"(model: "\${${RESULTS}.get(${element.eid}).model}") {
            builder.ul(class: "errors", role: "alert") {
                builder."g:eachError"(model: "\${${RESULTS}.get(${element.eid}).model}", var: "error") {
                    builder.li {
                        builder."g:message"(error: "\${error}")
                    }
                }
            }
        }
        
        // Now build the actual form...
        builder.form(method: "post", action: "#") {
            builder.input(type: "hidden", name: "id", value: "\${${RESULTS}.get(${element.eid}).get('${element.domainClass.name}').id}")
            builder.input(type: "hidden", name: "eid", value: element.eid)
            builder.fieldset(class: "form") {
                buildFormColumns(element.columns)
            }
            builder.fieldset(class: "buttons") {
                buildButtons(element.buttons)
            }
        }
    }
    
    /**
     * Builds a html table based on the information of the given element
     * @param element The container element which represents a table
     */
    private void buildTable(DataContainer element) {
        builder.div(class: "tbl_container") {
            builder.input(type: "hidden", name: "default-action", value: element.action)
            builder.div(class: "tbl_toolbar right") {
                builder.span("Export data: ")
                builder.select(class: "export-data") {
                    builder.option(value: "-1", " ")
                    builder.option(value: "export=${element.eid}&format=csv&sep=,", "CSV (,)")
                    builder.option(value: "export=${element.eid}&format=csv&sep=;", "CSV (;)")
                    builder.option(value: "export=${element.eid}&format=csv&sep=tab", "CSV (tab)")
                    builder.option(value: "export=${element.eid}&format=xls", "XLS")
                    builder.option(value: "export=${element.eid}&format=xml", "XML")
                }
            }
            builder.table(class: "clear") {
                builder.thead {
                    builder.tr {
                        buildTableHeader(element)
                    }
                    builder.tr {
                        buildTableFilters(element)
                    }
                }
                builder.tbody {
                    buildTableColumns(element)
                }
            }
        }
    }
    
    /**
     * Builds a html overview based on the information of the given element
     * @element The container element which represents a overview
     */
    private void buildOverview(DataContainer element) {
        builder.ol(class: "property-list") {
            buildOverviewColumns(element.columns)
        }
    }
    
    /**
     * Builds a html container based on the information of the given element
     * @param element The container element
     */
    private void buildContainer(ContainerElement element) {
        builder.div(class: "buttons") {
            buildButtons(element.buttons)
        }
    }
    
    /**
     * Builds the form columns based on the information of the given columns
     * @param columns The list of columns to be represented in the form
     */
    private void buildFormColumns(List<Column> columns) {
        columns.each { c ->
            GrailsDomainClassProperty p = c.property
            ConstrainedProperty cp = c.constrainedProperty

            boolean display = (cp ? cp.display : true)
            boolean required = (cp ? !(cp.propertyType in [boolean, Boolean]) && !cp.nullable && (cp.propertyType != String || !cp.blank) : false)

            // A column can be represented in several different ways:
            // As a parent column which contains other columns that can be created/edited multiple times
            if (display && c.hasElements() && c.multiple) {
                builder.div(class: "\${hasErrors(bean: ${RESULTS}.get(${c.root.eid}).get('${c.domainClass.name}'), field: '${c.name}', 'error')} ${required ? 'required' : ''}") {
                    builder.label(class: "property-label", for: "${c.domainClass.name}.${c.name}") {
                        builder."eca:fallbackMessage"(code: getCode(p), fbCode: getFbCode(p))

                        if (RenderEditor.isRequired(c) && !c.isReadOnly() && c.name != 'id') {
                            builder.span(class: "required-indicator", "*")
                        }
                    }
                    builder.ul(class: "property-value") {
                        builder."g:each"(in: "\${${RESULTS}.get(${c.root.eid}).get('${c.domainClass.name}')['${c.name}']}", var: "instance", status: "i") {
                            builder."g:if"(test: "\${!instance['deleted']}") {
                                builder.li {
                                    builder.input(type: "hidden", name: "${c.property.referencedDomainClass.name}_\${i}.id", value: "\${instance.id}")
                                    buildFormColumns(c.columns)
                                    builder.span(class: "ui-icon ui-icon-circle-minus", "")
                                }
                            }
                        }
                        builder.li(class: "add") {
                            builder.span(class: "ui-icon ui-icon-circle-plus", "")
                            builder."g:message"(code: "default.add.label", args: "[eca.fallbackMessage(code: '${getCode(p)}', fbCode: '${getFbCode(p)}').toLowerCase()]")
                            builder.input(type: "hidden",  name: "${c.property.referencedDomainClass.name}.to-be-deleted", class: "to-be-deleted")
                        }
                        builder.li(class: "hidden") {
                            builder.input(type: "hidden", name: "${c.property.referencedDomainClass.name}_null.id")
                            buildFormColumns(c.columns)
                            builder.span(class: "ui-icon ui-icon-circle-minus", "")
                        }
                    }
                }
            }
            // Or not, because it contains other columns; Build those instead 
            else if (display && c.hasElements()) {
                buildFormColumns(c.columns)
            }
            // As a child of another column which can be created/edited multiple times
            else if (display && c.parent instanceof Column && c.parent.multiple) {
                builder.label(class: "property-label") {
                    builder."eca:fallbackMessage"(code: getCode(p), fbCode: getFbCode(p))
                    renderEditor.render(c)
                }
            }
            // Or just simply as column which contains no other columns
            else if (display && !c.hidden) {
                builder.div(class: "\${hasErrors(bean: ${RESULTS}.get(${c.root.eid}).get('${c.domainClass.name}'), field: '${c.name}', 'error')} ${required ? 'required' : ''}") {
                    if (c.name == "id") {
                        builder.label(class: "property-label", for: "${c.domainClass.name}.${c.name}", "#")
                    }
                    else {
                        builder.label(class: "property-label", for: "${c.domainClass.name}.${c.name}") {
                            builder."eca:fallbackMessage"(code: getCode(p), fbCode: getFbCode(p))

                            if (RenderEditor.isRequired(c) && !c.isReadOnly() && c.name != 'id') {
                                builder.span(class: "required-indicator", "*")
                            }
                        }
                    }

                    if (c.name == 'id' || c.isReadOnly()) {
                        builder.span(class: "property-value", "\${${RESULTS}.get(${c.root.eid}).get('${c.domainClass.name}')['${c.name}'].encodeAsHTML()}")
                    }
                    else {
                        renderEditor.render(c)
                    }
                }
            }
        }
    }  
    
    /**
     * Builds the table columns based on the information of the given element
     * @param element The element to build table columns from
     */
    private void buildTableColumns(DataContainer element) {
        builder."g:each"(in: "\${${RESULTS}.get(${element.eid}).get()}", var: "row", status: "i") {
            builder.tr {
                if (element.index) {
                    builder.td(class: "counter", "\${i+1}")
                }
                if (!element.columns.find { it.name.equalsIgnoreCase("id") }) {
                    builder.td(class: "id hidden") {
                        builder."g:fieldValue"(bean: "\${row}", field: "id")
                    }
                }
                element.forAllColumns { c ->
                    if (!c.hasColumns() && (!c.constrainedProperty || c.constrainedProperty.display) && !c.hidden) {
                        if (c.name == "id") {
                            builder.td(class: "id") {
                                builder."g:fieldValue"(bean: "\${row}", field: "id")
                            }
                        }
                        else if (c.property.type == Date || c.property.type == java.sql.Date || c.property.type == java.sql.Time || c.property.type == Calendar) {
                            builder.td {
                                builder."g:formatDate"(date: "\${row.${c.columnPath.join('.')}}")
                            }
                        }
                        else if (c.property.type == Boolean || c.property.type == boolean) {
                            builder.td {
                                builder."g:formatBoolean"(boolean: "\${row.${c.columnPath.join('.')}}")
                            }
                        }
                        else  {
                            builder.td("\${row.${c.columnPath.join('.')}}")
                        }
                    }
                }
            }
        }           
    }
    
    /**
     * Builds the overview columns based on the information of the given columns
     * @param columns The list of columns to be represented in the overview
     */
    private void buildOverviewColumns(List<Column> columns) {
        columns.each { c -> 
            GrailsDomainClassProperty p = c.property
            ConstrainedProperty cp = c.constrainedProperty
            
            boolean display = (cp ? cp.display : true)

            // If the column contains other columns, build them instead
            if (c.hasElements()) {
                buildOverviewColumns(c.columns)
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
                            builder.li("\${element.encodeAsHTML()}")
                        }
                    }
                }
            }
            // Otherwise just print the column
            else if (display && !c.hidden) {
                builder.li {
                    if (c.name == "id") {
                        builder.span(id: "id-label", class: "property-label", "#")
                    }
                    else {
                        builder.span(id: "${c.name}-label", class: "property-label") {
                            builder."eca:fallbackMessage"(code: getCode(c.property), fbCode: getFbCode(c.property))
                        }
                    }

                    String value = "\${${RESULTS}.get(${c.root.eid}).get('${c.domainClass.name}')['${c.name}']}"
                    builder.span(class: "property-value", "arial-labelledby": "${c.name}-label") {
                        if (c.property.type == Boolean || c.property.type == boolean) {
                            builder."g:checkBox"(name: c.name, value: value, disabled: true)
                        }
                        else if (c.property.type == Date || c.property.type == java.sql.Date || c.property.type == java.sql.Time || c.property.type == Calendar) {
                            builder."g:formatDate"(date: value)
                        }
                        else {
                            builder."eca:formatText"(text: value)
                        }
                    }
                }
            }
        }
    }

    /**
     * Builds all of the buttons in the given list
     * @param buttons The list of buttons to be build
     */
    private void buildButtons(List<Button> buttons) {
        buttons.each { button -> 
            switch (button.type) {
                case Button.Type.SAVE:
                    builder.input(type: "submit", name: "btn_${button.name}", class: "btn_${button.name}", value: "\${message(code: 'default.button.${button.name}.label')}")
                    break
                case Button.Type.BACK:
                    builder."eca:link"(controller: "\${params.prevController}", action: "\${params.prevAction}", id: "\${params.prevId}") {
                        builder."g:message"(code: "default.button.${button.name}.label")
                    }
                    break
                case Button.Type.URL:
                default:
                    builder."eca:link"(controller: button.controller, action: button.action, id: button.id) {
                        builder."g:message"(code: "default.button.${button.action}.label")
                    }
            }
        }
    }
    
    /**
     * Build the header of the given element representing a table
     * @param element The element representing a table
     */
    private void buildTableHeader(DataContainer element) {
        if (element.index) {
            builder.th(class: "counter", "")
        }
        if (!element.columns.find { it.name.equalsIgnoreCase("id") }) {
            builder.th(class: "id hidden", "")
        }
        element.forAllColumns { c ->
            if (!c.hasColumns() && (!c.constrainedProperty || c.constrainedProperty.display) && !c.hidden) {
                if (c.name == "id") {
                    builder.th(class: "id sortable") {
                        builder.mkp.yield "#"
                        builder."eca:linkAllParams"(params: "['sort_${element.eid}_${c.name}': 'asc']") {
                            builder."g:img"(dir: "images/skin", file: "sorted_asc.gif", class: "sort_asc")
                        }
                        builder."eca:linkAllParams"(params: "['sort_${element.eid}_${c.name}': 'desc']") {
                            builder."g:img"(dir: "images/skin", file: "sorted_desc.gif", class: "sort_desc")
                        }
                    }
                }
                else {
                    builder.th(class: "sortable") {
                        builder."eca:fallbackMessage"(code: getCode(c.property), fbCode: getFbCode(c.property))

                        builder."eca:linkAllParams"(params: "['sort_${element.eid}_${c.name}': 'asc']") {
                            builder."g:img"(dir: "images/skin", file: "sorted_asc.gif", class: "sort_asc")
                        }
                        builder."eca:linkAllParams"(params: "['sort_${element.eid}_${c.name}': 'desc']") {
                            builder."g:img"(dir: "images/skin", file: "sorted_desc.gif", class: "sort_desc")
                        }
                    }
                }
            }
        }            
    }
        
    /**
     * Build the filters section of the given element representing a table
     * @param element The element representing a table
     */
    private void buildTableFilters(DataContainer element) {
        if (element.index) {
            builder.th(class: "counter", "")
        }
        if (!element.columns.find { it.name.equalsIgnoreCase("id") }) {
            builder.th(class: "id hidden", "")
        }
        element.forAllColumns { c ->
            if (!c.hasColumns() && (!c.constrainedProperty || c.constrainedProperty.display) && !c.hidden) {
                builder.th(class: "filter", value: "\${params.filter_${element.eid}_${c.name}}") {
                    if (c.property.type == Boolean || c.property.type == boolean) {
                        builder."eca:booleanSelect"(name: "filter_${element.eid}_${c.name}", value: "\${params.filter_${element.eid}_${c.name}}")
                    }
                    else if (c.property.type == String)  {
                        builder.input(type: "text", name: "filter_${element.eid}_${c.name}", value: "\${params.filter_${element.eid}_${c.name}}", placeholder: "Filter on \${eca.fallbackMessage(code: '${getCode(c.property)}', fbCode: '${getFbCode(c.property)}').toLowerCase()}")
                    }
                }
            }
        }
    }
    
    /**
     * Returns the i18n lookup code for the given column property
     * @param property The propery of a column which needs an i18n label
     */
    static getCode(GrailsDomainClassProperty property) {
        if (property.name.equalsIgnoreCase("enabled") || property.name.equalsIgnoreCase("deleted")) {
            return "default.${property.name}.label"
        }
        else if (property.manyToOne || property.oneToOne || property.oneToMany || property.manyToMany) {
            return "${property.referencedDomainClass.propertyName}.multiple.label"
        }
        else {
            return "${property.domainClass.propertyName}.${property.name}.label"
        }
    }
    
    /**
     * Returns the i18n fallback lookup code for the given column property, in case the first one does not exist
     * @param property The propery of a column which needs a secondary i18n label
     */
    static getFbCode(GrailsDomainClassProperty property) {
        "${property.domainClass.propertyName}.${property.name}.label"
    }
}
