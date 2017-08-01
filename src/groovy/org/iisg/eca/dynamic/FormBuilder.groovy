package org.iisg.eca.dynamic

import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.validation.ConstrainedProperty

/**
 * Builds a html form based on the given element
 */
class FormBuilder extends ElementBuilder {
    private RenderEditor renderEditor

    /**
     * Creates a new <code>FormBuilder</code> for use with container elements
     * @param builder The markup builder
     * @param renderEditor The render editor
     */
    FormBuilder(MarkupBuilder builder, RenderEditor renderEditor) {
        this.builder = builder
        this.renderEditor = renderEditor
    }

    /**
     * Builds a html form based on the information of the given element
     * @param element The container element which represents a form
     */
    void build(DataContainer element) {
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
        builder.form(method: "post", action: "#", class: element.classNames) {
            builder.input(type: "hidden", name: "id", value: "\${${RESULTS}.get(${element.eid}).get('${element.domainClass.name}').id}")
            builder.input(type: "hidden", name: "eid", value: element.eid)
            builder.fieldset(class: "form") {
                buildColumns(element.columns)
            }
            builder.fieldset(class: "buttons") {
                buildButtons(element.buttons)
            }
        }
    }

    /**
     * Builds the form columns based on the information of the given columns
     * @param columns The list of columns to be represented in the form
     */
    private void buildColumns(List<Column> columns) {
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
                            builder."g:if"(test: "\${!instance.domainClass.hasProperty('deleted') || !instance.deleted}") {
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
                            builder.input(type: "hidden", name: "${c.property.referencedDomainClass.name}.to-be-deleted", class: "to-be-deleted")
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
                buildColumns(c.columns)
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
                    } else {
                        builder.label(class: "property-label", for: "${c.domainClass.name}.${c.name}") {
                            builder."eca:fallbackMessage"(code: getCode(p), fbCode: getFbCode(p))

                            if (RenderEditor.isRequired(c) && !c.isReadOnly() && c.name != 'id') {
                                builder.span(class: "required-indicator", "*")
                            }
                        }
                    }

                    if (c.name == 'id' || c.isReadOnly()) {
                        builder.span(class: "property-value", "\${${RESULTS}.get(${c.root.eid}).get('${c.domainClass.name}')['${c.name}'].encodeAsHTML()}")
                    } else {
                        builder.span(class: "property-value") {
                            renderEditor.render(c)
                        }
                    }
                }
            }
        }
    }
}
