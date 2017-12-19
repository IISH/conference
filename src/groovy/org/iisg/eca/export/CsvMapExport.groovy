package org.iisg.eca.export

import groovy.transform.CompileStatic

/**
 * Export csv files
 */
@CompileStatic
class CsvMapExport extends MapExport {
    private static final String CONTENT_TYPE = 'text/csv'
    private static final String EXTENSION = 'csv'

    private String separator = ','

    /**
     * Creates a new csv export for the specified results
     * @param columns An array of columns to export
     * @param results The results, a list with arrays of domain classes, to export
     * @param title The title of the resulting file
     * @param columnNames The names of the columns
     */
    CsvMapExport(List<String> columns, List<Map> results, String title, List<String> columnNames) {
        super(columns, results, title, columnNames)
    }

    /**
     * Sets the separator to separate the values in the resulting document
     * @param separator The character(s) to separate the values with; 'tab' is reserved for separation by tabs
     */
    void setSeparator(String separator) {
        if (separator.equalsIgnoreCase('tab')) {
            this.separator = '\t'
        }
        else if (separator && !separator.isEmpty()) {
            this.separator = separator
        }
    }

    /**
     * Returns the content type of csv files
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
     * Parses the results to a csv file
     */
    @Override
    parse() {
        StringWriter writer = new StringWriter()
        writer.write("${columnNames.join(separator)}\r\n")

        results.each { result ->
            List<String> r = columns.collect { c ->
                if (result[c] != null) {
                    result[c].toString()
                }
                else {
                    ''
                }
            }

            writer.write("${r.join(separator)}\r\n")
        }

        writer
    }
}
