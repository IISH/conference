package org.iisg.eca.export

/**
 * Export csv files
 */
class CsvExport extends AbstractExport {
    private static final String CONTENT_TYPE = 'text/csv'

    private String seperator = ','

    /**
     * Creates a new csv export for the specified results
     * @param columns An array of columns to export
     * @param results The results, a list with arrays of domain classes, to export
     * @param title The title of the resulting file
     */
    CsvExport(List columns, List results, String title) {
        super(columns, results, title)
    }

    /**
     * Sets the seperator to seperate the values in the resulting document
     * @param seperator The character(s) to seperate the values with; 'tab' is reserved for seperation by tabs
     */
    void setSeperator(String seperator) {
        if (seperator.equalsIgnoreCase('tab')) {
            this.seperator = '\t'
        }
        else if (seperator && !seperator.isEmpty()) {
            this.seperator = seperator
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
     * Parses the results to a csv file
     */
    @Override
    parse() {
        StringWriter writer = new StringWriter()

        writer.write("${columnNames.join(seperator)}\n")
        results.each { result ->
            def r
            if (result.class.isArray()) {
                r = []
                columns.each { c -> r.add(result.find { it.class.simpleName == c.domainClass.name }[c.name]) }
            }
            else {
                r = columns.collect { result[it.name] }
            }
            writer.write("${r.join(seperator)}\n")
        }

        writer
    }
}
