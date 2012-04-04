package org.iisg.eca

import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib

/**
 * A parser to create xml files
 */
class XmlParser implements Parser {
    private static final String CONTENT_TYPE = 'application/xml'
    private static final ValidationTagLib MESSAGES = new ValidationTagLib()

    private String title
    private PageElement view

    /**
     * Creates a new xml parser for the specified element
     * @param pageElement The element to parse
     * @param title The title of the resulting file
     */
    XmlParser(PageElement view, String title) {
        this.view = view
        this.title = title
    }

    /**
     * Returns the content type of xml files
     * @return The content type
     */
    @Override
    def getContentType() {
        CONTENT_TYPE
    }

    /**
     * Parses the results to an xml file
     */
    @Override
    def parse() {
        def columns = view.allColumns
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        writer.write('<?xml version="1.0" encoding="utf-8" ?>\n')
        xml."${title}"() {
            view.result.eachWithIndex { r, i ->
                def columnMap = [:]
                columnMap.put('row', i)
                columns.each { column ->
                    def name = view.getDomainClassByColumn(column)
                    def columnName = MESSAGES.message(code: "${name.toLowerCase()}.${column.toLowerCase()}.label")?.toLowerCase()
                    columnMap.put(columnName, r[column])
                }
                row(columnMap)
            }
        }

        writer
    }
}