package org.iisg.eca.export

import org.iisg.eca.tags.UtilsTagLib

import org.iisg.eca.dynamic.Column
import org.iisg.eca.dynamic.PageBuilder

/**
 * An abstract implementation of the <code>Export</code> interface
 */
abstract class DynamicPageExport implements Export {
    /**
     * Required for looking up the translated column names
     */
    protected static final UtilsTagLib UTILS = new UtilsTagLib()

    protected List<Column> columns
    protected List results
    protected String title
    protected List<String> columnNames

    /**
     * Sets up the object with the required parameters
     * @param columns A list of columns to export
     * @param results A list of results to export
     * @param title The title of the resulting file
     */
    DynamicPageExport(List columns, List results, String title) {
        this.columns = columns
        this.results = results
        this.title = title
        columnNames = columns.collect { Column c ->
            if (!c.hasColumns() && (!c.constrainedProperty || c.constrainedProperty.display) && !c.hidden) {
                if (c.name == "id") {
                    "id"
                }
                else {
                    UTILS.fallbackMessage(code: PageBuilder.getCode(c.property), fbCode: PageBuilder.getFbCode(c.property))
                }
            }
        }
        columnNames.removeAll { it == null }
    }

    /**
     * Returns the title of the resulting file
     * @return The title
     */
    @Override
    String getTitle() {
        return title
    }
}
