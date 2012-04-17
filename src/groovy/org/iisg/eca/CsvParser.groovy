package org.iisg.eca

import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib

/**
 * A parser to create csv files
 */
class CsvParser implements Parser {
    private static final String CONTENT_TYPE = 'text/csv'
    private static final ValidationTagLib MESSAGES = new ValidationTagLib()

    private String title
    private DataContainer pageElement
    private String seperator = ','

    /**
     * Creates a new csv parser for the specified element
     * @param pageElement The element to parse
     * @param title The title of the resulting file
     */
    CsvParser(DataContainer pageElement, String title) {
        this.pageElement = pageElement
        this.title = title
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
    def parse() {
        def columns = view.allColumns
        def writer = new StringWriter()

        def colummNames = columns.collect { column ->
            def name = view.getDomainClassByColumn(column)
            MESSAGES.message(code: "${name.toLowerCase()}.${column.toLowerCase()}.label")
        }
        writer.write("${colummNames.join(seperator)}\n")

        view.result.eachWithIndex { r, i ->
            writer.write("${columns.collect { r[it] }.join(seperator)}\n")
        }

        writer
    }
}
