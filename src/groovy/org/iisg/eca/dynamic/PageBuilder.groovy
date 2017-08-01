package org.iisg.eca.dynamic

import groovy.xml.MarkupBuilder

/**
 * Builds a html page based on the elements
 */
class PageBuilder extends ElementBuilder {
    private List<ContainerElement> pageElements
    private StringWriter writer
    private RenderEditor renderEditor

    private FormBuilder formBuilder
    private TableBuilder tableBuilder
    private OverviewBuilder overviewBuilder

    /**
     * Creates a new <code>PageBuilder</code> for the list of container elements
     * @param pageElements A list of all elements that have to be present on the page
     */
    PageBuilder(List<ContainerElement> pageElements) {
        this.pageElements = pageElements
        this.writer = new StringWriter()
        this.builder = new MarkupBuilder(writer)
        this.builder.doubleQuotes = true
        this.builder.escapeAttributes = false
        this.renderEditor = new RenderEditor(builder, RESULTS)

        this.formBuilder = new FormBuilder(this.builder, this.renderEditor)
        this.tableBuilder = new TableBuilder(this.builder, this.renderEditor)
        this.overviewBuilder = new OverviewBuilder(this.builder)
    }

    /**
     * Builds the page and returns the resulting html mixed with GSP tags,
     * ready to be rendered with the data from the database
     * @returns A GSP template
     */
    String buildPage() {
        pageElements.each { element ->
            if (element instanceof DataContainer) {
                switch (element.type) {
                    case DataContainer.Type.FORM:
                        formBuilder.build(element)
                        break
                    case DataContainer.Type.TABLE:
                        tableBuilder.build(element)
                        break
                    case DataContainer.Type.OVERVIEW:
                        overviewBuilder.build(element)
                }
            } else {
                build(element)
            }
        }
        writer.toString()
    }

    /**
     * Builds a html container based on the information of the given element
     * @param element The container element
     */
    private void build(ContainerElement element) {
        builder.div(class: "buttons") {
            buildButtons(element.buttons)
        }
    }
}
