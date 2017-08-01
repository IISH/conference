package org.iisg.eca.dynamic

import groovy.xml.MarkupBuilder

/**
 * Builds a html table based on the given element
 */
class TableBuilder extends ElementBuilder {
    private RenderEditor renderEditor

    /**
     * Creates a new <code>TableBuilder</code> for use with container elements
     * @param builder The markup builder
     * @param renderEditor The render editor
     */
    TableBuilder(MarkupBuilder builder, RenderEditor renderEditor) {
        this.builder = builder
        this.renderEditor = renderEditor
    }

    /**
     * Builds a html table based on the information of the given element
     * @param element The container element which represents a table
     */
    void build(DataContainer element) {
        builder.div(class: "tbl_container ${element.classNames}") {
            if (element.action) {
                builder.input(type: "hidden", name: "url", value: "\${eca.createLinkAllParams(controller: params.controller, action: '${element.action}', id: 0)}")
            }

            builder.div(class: "menu") {
                builder.ul {
                    builder.li {
                        builder.a(href: "", "Open link")
                    }
                    builder.li {
                        builder.a(href: "", target: "_blank", "Open link in new tab")
                    }
                }
            }

            builder.div(class: "tbl_toolbar right") {
                builder.span {
                    builder."g:message"(code: "default.export.data")
                }
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
                        buildHeader(element)
                    }
                    builder.tr {
                        buildFilters(element)
                    }
                }
                builder.tbody {
                    buildColumns(element)
                }
            }

            if (element.totals) {
                builder.div(class: "tbl_totals") {
                    builder.span("\${g.message(code: 'default.totals.filtered')}: \${${RESULTS}.get(${element.eid}).get().size()}")
                    builder.span("\${g.message(code: 'default.totals.not.filtered')}: \${${RESULTS}.get(${element.eid}).getUnfiltered().size()}")
                }
            }
        }
    }

    /**
     * Build the header of the given element representing a table
     * @param element The element representing a table
     */
    private void buildHeader(DataContainer element) {
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

                        if (!c.hideSorting) {
                            builder.span(class: "sort_asc asc_unselected", name: "${element.eid}|${c.name}", "")
                            builder.span(class: "sort_desc desc_unselected", name: "${element.eid}|${c.name}", "")
                        }
                    }
                } else {
                    builder.th(class: "sortable") {
                        builder."eca:fallbackMessage"(code: getCode(c.property), fbCode: getFbCode(c.property))

                        if (!c.hideSorting) {
                            builder.span(class: "sort_asc asc_unselected", name: "${element.eid}|${c.name}", "")
                            builder.span(class: "sort_desc desc_unselected", name: "${element.eid}|${c.name}", "")
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
    private void buildFilters(DataContainer element) {
        if (element.index) {
            builder.th(class: "counter", "")
        }
        if (!element.columns.find { it.name.equalsIgnoreCase("id") }) {
            builder.th(class: "id hidden", "")
        }
        element.forAllColumns { c ->
            if (!c.hasColumns() && (!c.constrainedProperty || c.constrainedProperty.display) && !c.hidden) {
                builder.th(class: "filter", value: "\${params.filter_${element.eid}_${c.name}}") {
                    if (!c.hideFilter) {
                        if (c.property.type == Boolean || c.property.type == boolean) {
                            builder."eca:booleanSelect"(name: "filter_${element.eid}_${c.name}", value: "\${params.filter_${element.eid}_${c.name}}")
                        } else if (c.property.type == String || ((c.property.oneToMany || c.property.manyToMany) && c.filterColumn)) {
                            builder.input(type: "text", name: "filter_${element.eid}_${c.name}", value: "\${params.filter_${element.eid}_${c.name}}", placeholder: "\${g.message(code: 'default.filter.on')} \${eca.fallbackMessage(code: '${getCode(c.property)}', fbCode: '${getFbCode(c.property)}').toLowerCase()}")
                        } else if (c.property.manyToOne || c.property.oneToOne) {
                            builder."g:set"(var: "${c.name}ListCache", value: "\${${c.property.type.name}.list()}")

                            if (c.filter) {
                                renderEditor.render(c, [name: "filter_${element.eid}_${c.name}", value: "\${params.filter_${element.eid}_${c.name}}", noSelection: "\${[null: '']}", from: "\${${c.name}ListCache}"])
                            } else {
                                renderEditor.render(c, [name: "filter_${element.eid}_${c.name}", value: "\${params.filter_${element.eid}_${c.name}}", noSelection: "\${[null: 'No filter selected']}", from: "\${${c.name}ListCache}"])
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Builds the table columns based on the information of the given element
     * @param element The element to build table columns from
     */
    private void buildColumns(DataContainer element) {
        builder."g:each"(in: "\${${RESULTS}.get(${element.eid}).get()}", var: "row", status: "i") {
            builder.tr {
                if (element.index) {
                    builder.td(class: "counter", "\${i+1}")
                }
                if (element.getAllColumns().find { it.isId() }) {
                    Column column = element.getAllColumns().find { it.isId() }
                    builder.td(class: "id hidden", "\${row.${column.columnPath.join('.')}}")
                } else if (!element.columns.find { it.name.equalsIgnoreCase("id") }) {
                    builder.td(class: "id hidden", "\${row.id}")
                }
                element.forAllColumns { c ->
                    if (c.canBeShown()) {
                        if (c.hidden) {
                            builder.td(class: "hidden", "\${row.${c.columnPath.join('.')}}")
                        } else if (c.name == "id") {
                            builder.td(class: "id", "\${row.id}")
                        } else if (c.interactive && (c.property.manyToOne || c.property.oneToOne)) {
                            builder.td {
                                builder."eca:radioSelect"(name: c.interactive + "\${i+1}", class: c.interactive, labelName: "shortDescription", "value": "\${row.${c.columnPath.join('.')}.id}", values: "\${${c.name}ListCache}")

                                builder."span"(class: "ui-icon ui-icon-check invisible", "")
                                builder."span"(class: "ui-icon ui-icon-alert invisible", "")
                            }
                        } else if (c.interactive && c.name.equals("gender")) {
                            builder.td {
                                builder."label" {
                                    builder."g:radio"(name: c.interactive + "\${i+1}", class: c.interactive, value: "M", checked: "\${row.${c.columnPath.join('.')}.equals('M')}")
                                    builder.mkp.yield " M"
                                }
                                builder.mkp.yieldUnescaped "&nbsp;"
                                builder."label" {
                                    builder."g:radio"(name: c.interactive + "\${i+1}", class: c.interactive, value: "F", checked: "\${row.${c.columnPath.join('.')}.equals('F')}")
                                    builder.mkp.yield " F"
                                }
                                builder.mkp.yieldUnescaped "&nbsp;"
                                builder."label" {
                                    builder."g:radio"(name: c.interactive + "\${i+1}", class: c.interactive, value: "null", checked: "\${row.${c.columnPath.join('.')} == null}")
                                    builder.mkp.yield " Unknown"
                                }
                                builder.mkp.yieldUnescaped "&nbsp;"

                                builder."span"(class: "ui-icon ui-icon-check invisible", "")
                                builder."span"(class: "ui-icon ui-icon-alert invisible", "")
                            }
                        } else if (c.property.type == Date || c.property.type == java.sql.Date || c.property.type == java.sql.Time || c.property.type == Calendar) {
                            builder.td {
                                builder."g:formatDate"(date: "\${row.${c.columnPath.join('.')}}")
                            }
                        } else if (c.property.type == Boolean || c.property.type == boolean) {
                            builder.td {
                                builder."g:formatBoolean"(boolean: "\${row.${c.columnPath.join('.')}}")
                            }
                        } else if (c.property.oneToMany || c.property.manyToMany) {
                            builder.td("\${row.${c.columnPath.join('.')}.join(', ')}")
                        } else {
                            builder.td("\${row.${c.columnPath.join('.')}}")
                        }
                    }
                }
            }
        }
    }
}
