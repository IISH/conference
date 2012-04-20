package org.iisg.eca.export

import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib
import org.iisg.eca.dynamic.ContainerElement

/**
 * An abstract implementation of the <code>Export</code> interface
 */
abstract class AbstractExport implements Export {
    /**
     * Required for looking up the translated column names
     */
    protected static final ValidationTagLib MESSAGES = new ValidationTagLib()

    protected List columns
    protected List results
    protected String title
    protected List columnNames

    /**
     * Sets up the object with the required parameters
     * @param columns A list of columns to export
     * @param results A list of results to export
     * @param title The title of the resulting file
     */
    AbstractExport(List columns, List results, String title) {
        this.columns = columns
        this.results = results
        this.title = title
        columnNames = columns.collect { MESSAGES.message(code: "${it.domainClass.name.toLowerCase()}.${it.name.toLowerCase()}.label") }
    }
}
