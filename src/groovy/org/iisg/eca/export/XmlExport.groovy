package org.iisg.eca.export

import groovy.xml.MarkupBuilder
import java.util.regex.Pattern

/**
 * Export xml files
 */
class XmlExport extends AbstractExport {
    private static final String CONTENT_TYPE = 'application/xml'
    private static final Pattern INVALID_CHARS = Pattern.compile("[^a-zA-Z0-9]")

    /**
     * Creates a new xml export for the specified element
     * @param columns An array of columns to export
     * @param results The results, a list with arrays of domain classes, to export
     * @param title The title of the resulting file
     */
    XmlExport(List columns, List results, String title) {
        super(columns, results, title)
    }

    /**
     * Returns the content type of xml files
     * @return The content type
     */
    @Override
    String getContentType() {
        CONTENT_TYPE
    }

    /**
     * Exports the results to an xml file
     */
    @Override
    parse() {
        StringWriter writer = new StringWriter()
        MarkupBuilder xml = new MarkupBuilder(writer)
        xml.doubleQuotes = true

        writer.write('<?xml version="1.0" encoding="utf-8" ?>\n')

        xml."${escapeString(title.toLowerCase())}"() {
            results.eachWithIndex { r, i ->
                xml.row("${escapeString(UTILS.message(code: 'default.count').toLowerCase())}": i+1) {
                    columns.grep { it.canBeShown() }.eachWithIndex { c, j ->
                        def value = r
                        c.columnPath.each { value = value[it.toString()] }
                        xml."${escapeString(columnNames[j].toLowerCase())}"(value)
                    }
                }
            }
        }

        writer
    }

    /**
     * Makes sure all illegal characters are replaced by an underscore (_)
     * @param text The text which may contain illegal characters
     * @return The valid text to use in xml document
     */
    private String escapeString(String text) {
        text = text.trim()
        INVALID_CHARS.matcher(text).replaceAll("_")
    }
}