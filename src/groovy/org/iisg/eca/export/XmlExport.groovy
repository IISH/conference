package org.iisg.eca.export

import groovy.xml.MarkupBuilder

/**
 * Export xml files
 */
class XmlExport extends AbstractExport {
    private static final String CONTENT_TYPE = 'application/xml'

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
        xml."${title.toLowerCase().replaceAll('\\s', '-')}"() {
            results.eachWithIndex { r, i ->
                xml.row("${UTILS.message(code: 'default.count').toLowerCase()}": i+1) {
                    columns.eachWithIndex { c, j ->
                        xml."${columnNames[j].toLowerCase().replaceAll('\\s', '-')}"(r."${c.columnPath.join('.')}")
                    }
                }
            }
        }

        writer
    }
}