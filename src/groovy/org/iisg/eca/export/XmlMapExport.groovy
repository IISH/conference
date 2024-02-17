package org.iisg.eca.export

import java.util.regex.Pattern
import groovy.xml.MarkupBuilder
import org.iisg.eca.tags.UtilsTagLib

/**
 * Export xml files
 */
class XmlMapExport extends MapExport {
    /**
    * Required for looking up the translated column names
    */
    protected static final UtilsTagLib UTILS = new UtilsTagLib()

    private static final String CONTENT_TYPE = 'application/xml'
    private static final String EXTENSION = 'xml'
    private static final Pattern INVALID_CHARS = Pattern.compile("[^a-zA-Z0-9]")

    /**
     * Creates a new xml export for the specified element
     * @param columns An array of columns to export
     * @param results The results, a list with arrays of domain classes, to export
     * @param title The title of the resulting file
     * @param columnNames The names of the columns
     */
    XmlMapExport(List<String> columns, List<Map> results, String title, List<String> columnNames) {
        super(columns, results, title, columnNames)
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
     * Returns the extension of csv files
     * @return The extension
     */
    @Override
    String getExtension() {
        EXTENSION
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
                    columns.eachWithIndex { c, j ->
                        if ((r[c] != null) && !r[c].toString().trim().isEmpty()) {
                            xml."${escapeString(columnNames[j].toLowerCase())}"(r[c].toString())
                        }
                        else {
                            xml."${escapeString(columnNames[j].toLowerCase())}"()
                        }
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
    private static String escapeString(String text) {
        text = text.trim()
        INVALID_CHARS.matcher(text).replaceAll("_")
    }
}