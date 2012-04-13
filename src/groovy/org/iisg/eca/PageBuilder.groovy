package org.iisg.eca

import groovy.xml.MarkupBuilder

import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty

class PageBuilder {
    private static final String RESULTS = "results"
    
    private Map<Integer, PageElement> pageElements  
    private StringWriter writer
    private MarkupBuilder builder
    private RenderEditor renderEditor
    
    PageBuilder(Map<Integer, PageElement> pageElements) {
        this.pageElements = pageElements
        this.writer = new StringWriter()
        this.builder = new MarkupBuilder(writer)
        this.builder.doubleQuotes = true
        this.renderEditor = new RenderEditor(builder, RESULTS)
    }
    
    String buildPage() {
        pageElements.values().each { element -> 
            switch(element.type) {
                case PageElement.Type.FORM:
                    buildForm(element) 
                    break
                case PageElement.Type.TABLE:
                    buildTable(element) 
                    break
                case PageElement.Type.OVERVIEW:
                    buildOverview(element) 
                    break
                case PageElement.Type.BUTTONS:
                    buildButtonsHome(element) 
                    break
            }
        }
        writer.toString()
    }
    
    private void buildForm(PageElement element) {
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
    
    private void buildTable(PageElement element) {
        builder.div(class: "tbl_container") {
            builder.div(class: "tbl_toolbar right") {
                builder.mkp.yield("Export data: ")
                builder."eca:linkAllParams"(params: "['format': 'csv', 'sep': ',']", "CSV (,)")
                builder."eca:linkAllParams"(params: "['format': 'csv', 'sep': ';']", "CSV (;)")
                builder."eca:linkAllParams"(params: "['format': 'csv', 'sep': 'tab']", "CSV (tab)")
                builder."eca:linkAllParams"(params: "['format': 'xls']", "XLS")
                builder."eca:linkAllParams"(params: "['format': 'xml']", "XML")
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
    
    private void buildOverview(PageElement element) {
        builder.ol(class: "property-list") {
            buildOverviewColumns(element.columns)
        }
    }
    
    private void buildButtonsHome(PageElement element) {
        builder.div(class: 'buttons') {
            buildButtons(element.buttons)
        }
    }
    
    private void buildFormColumns(List<Column> columns) {
        columns.each { c ->
            GrailsDomainClassProperty p = c.property
            ConstrainedProperty cp = c.constrainedProperty

            boolean display = (cp ? cp.display : true)
            boolean required = (cp ? !(cp.propertyType in [boolean, Boolean]) && !cp.nullable && (cp.propertyType != String || !cp.blank) : false)

            if (display && c.hasChildren() && c.multiple) {
                builder.div(class: "fieldcontain \${hasErrors(bean: ${RESULTS}.get(${c.pageElement.eid}).get('${c.domainClass.name}'), field: '${c.name}', 'error')} ${required ? 'required' : ''}") {
                    builder.label(for: c.name) {
                        builder."eca:fallbackMessage"(code: getCode(p), fbCode: getFbCode(p))

                        if (RenderEditor.isRequired(c) && !c.isReadOnly() && c.name != 'id') {
                            builder.span(class: "required-indicator", "*")
                        }
                    }
                    builder.ul(class: "inline") {
                        builder."g:each"(in: "\${${RESULTS}.get(${c.pageElement.eid}).get('${c.domainClass.name}')['${c.name}']}", var: "instance", status: "i") {
                            builder.li {
                                buildFormColumns(c.children)
                                builder.span(class: "ui-icon ui-icon-circle-minus", "")
                            }
                        }
                        builder.li(class: "add") {
                            builder.span(class: "ui-icon ui-icon-circle-plus", "")
                            builder."g:message"(code: "default.add.label", args: "[eca.fallbackMessage(code: '${getCode(p)}', fbCode: '${getFbCode(p)}').toLowerCase()]")
                        }
                        builder.li(class: "hidden") {
                            buildFormColumns(c.children)
                            builder.span(class: "ui-icon ui-icon-circle-minus", "")
                        }
                    }
                }
            }
            else if (display && c.hasChildren()) {
                buildFormColumns(c.children)
            }
            else if (display && c.parent?.multiple) {
                renderEditor.render(c)
            }
            else if (display) {
                builder.div(class: "fieldcontain \${hasErrors(bean: ${RESULTS}.get(${c.pageElement.eid}).get('${c.domainClass.name}'), field: '${c.name}', 'error')} ${required ? 'required' : ''}") {
                    if (c.name == "id") {
                        builder.label(for: c.name, "#")
                    }
                    else {
                        builder.label(for: c.name) {
                            builder."eca:fallbackMessage"(code: getCode(p), fbCode: getFbCode(p))

                            if (RenderEditor.isRequired(c) && !c.isReadOnly() && c.name != 'id') {
                                builder.span(class: "required-indicator", "*")
                            }
                        }
                    }

                    if (c.name == 'id' || c.isReadOnly()) {
                        builder.span("\${${RESULTS}.get(${c.pageElement.eid}).get('${c.domainClass.name}')['${c.name}'].encodeAsHTML()}")
                    }
                    else {
                        renderEditor.render(c)
                    }
                }
            }
        }
    }  
    
    private void buildTableColumns(PageElement element) {
        builder."g:each"(in: "\${${RESULTS}.get(${element.eid}).get()}", var: "row") {
            builder.tr {
                element.forAllColumns { c ->
                    if (!c.constrainedProperty  || c.constrainedProperty.display) {
                        builder.td {
                            builder."g:fieldValue"(bean: "\${row}", field: c.name)
                        }
                    }
                }
            }
        }           
    }
    
    private void buildOverviewColumns(List<Column> columns) {
        columns.each { c -> 
            GrailsDomainClassProperty p = c.property
            ConstrainedProperty cp = c.constrainedProperty
            
            boolean display = (cp ? cp.display : true)

            if (c.hasChildren()) {
                buildOverviewColumns(c.children)
            }
            else if (display && Collection.class.isAssignableFrom(c.property.type)) {                
                String inVar = "\${${RESULTS}.get(${c.pageElement.eid}).get('${c.domainClass.name}')['${c.name}']}"
                builder."g:each"(in: inVar, var: "element", status: "i") {
                    builder.li {
                        builder."g:if"(test: "\${i == 0}") {
                            builder.span(id: "${c.name}-label", class: "property-label") {
                                builder."eca:fallbackMessage"(code: getCode(c.property), fbCode: getFbCode(c.property))
                            }
                        }
                        builder."g:else" {
                            builder.span(class: "property-label", " ")
                        }
                        builder.span(class: "property-value", "arial-labelledby": "${c.name}-label", "\${element.encodeAsHTML()}")
                    }
                }
            }
            else if (display) {
                builder.li {
                    if (c.name == "id") {
                        builder.span(id: "id-label", class: "property-label", "#")
                    }
                    else {
                        builder.span(id: "${c.name}-label", class: "property-label") {
                            builder."eca:fallbackMessage"(code: getCode(c.property), fbCode: getFbCode(c.property))
                        }
                    }
                    
                    String value = "\${${RESULTS}.get(${c.pageElement.eid}).get('${c.domainClass.name}')['${c.name}']}"
                    builder.span(class: "property-value", "arial-labelledby": "${c.name}-label") {
                        if (c.property.type == Boolean || c.property.type == boolean) {
                            builder."g:checkBox"(name: c.name, value: value, disabled: true)
                        }
                        else if (c.property.type == Date || c.property.type == java.sql.Date || c.property.type == java.sql.Time || c.property.type == Calendar) {
                            builder."g:formatDate"(date: value)
                        }
                        else {
                            builder."g:fieldValue"(bean: "\${${RESULTS}.get(${c.pageElement.eid}).get('${c.domainClass.name}')}", field: c.name)
                        }
                    }
                }
            }
        }
    }

    private void buildButtons(List<Button> buttons) {
        buttons.each { button -> 
            switch (button.buttonType) {
                case Button.ButtonType.SAVE:
                    builder.input(type: "submit", name: "btn_${button.name}", class: "btn_${button.name}", value: "\${message(code: 'default.button.${button.name}.label')}")
                    break
                case Button.ButtonType.BACK:
                    builder."eca:link"(controller: "\${params.prevController}", action: "\${params.prevAction}", id: "\${params.prevId}") {
                        builder."g:message"(code: "default.button.${button.name}.label")
                    }
                    break
                case Button.ButtonType.URL:
                default:
                    builder."eca:link"(controller: button.controller, action: button.action, id: button.id) {
                        builder."g:message"(code: "default.button.${button.action}.label")
                    }
            }
        }
    }
    
    private void buildTableHeader(PageElement element) {
        element.forAllColumns { c -> 
            builder.th(class: "sortable") {
                builder."eca:fallbackMessage"(code: getCode(c.property), fbCode: getFbCode(c.property))

                builder."eca:linkAllParams"(params: "['sort_${element.eid}_${c.name}': 'asc']") {
                    builder."g:img"(dir: "images/skin", file: "sorted_asc_white.gif", class: "sort_asc")
                }
                builder."eca:linkAllParams"(params: "['sort_${element.eid}_${c.name}': 'desc']") {
                    builder."g:img"(dir: "images/skin", file: "sorted_desc_white.gif", class: "sort_desc")
                }
            }
        }            
    }
        
    private void buildTableFilters(PageElement element) {
        element.forAllColumns { c -> 
            builder.th(class: "filter") {
                builder.input(type: "text", name: "filter${element.eid}_${c.name}", value: "\${params.filter${element.eid}_${c.name}}", placeholder: "Filter on \${message(code: '${getCode(c.property)}').toLowerCase()}")
            }
        }
    }
    
    private static getCode(GrailsDomainClassProperty property) {
        if (property.name.equalsIgnoreCase("enabled") || property.name.equalsIgnoreCase("deleted")) {
            return "default.${property.name.toLowerCase()}.label"
        }
        else if (property.manyToOne || property.oneToOne || property.oneToMany || property.manyToMany) {
            return "${property.getReferencedDomainClass().name.toLowerCase()}.multiple.label"
        }
        else {
            return "${property.domainClass.propertyName.toLowerCase()}.${property.name.toLowerCase()}.label"
        }
    }
    
    private static getFbCode(GrailsDomainClassProperty property) {
        "${property.domainClass.propertyName.toLowerCase()}.${property.name.toLowerCase()}.label"      
    }
}
