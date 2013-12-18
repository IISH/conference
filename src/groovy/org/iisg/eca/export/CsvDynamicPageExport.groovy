package org.iisg.eca.export
/**
 * Export csv files
 */
class CsvDynamicPageExport extends DynamicPageExport {
    private static final String CONTENT_TYPE = 'text/csv'
    private static final String EXTENSION = 'csv'

    private String separator = ','

    /**
     * Creates a new csv export for the specified results
     * @param columns An array of columns to export
     * @param results The results, a list with arrays of domain classes, to export
     * @param title The title of the resulting file
     */
    CsvDynamicPageExport(List columns, List results, String title) {
        super(columns, results, title)
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
            List<String> r = columns.grep { it.canBeShown() && !it.isHidden() }.collect { c ->
                def value = result
                c.columnPath.each { value = value[it.toString()] }
                value
            }

            writer.write("${r.join(separator)}\r\n")
        }

        writer
    }
}
