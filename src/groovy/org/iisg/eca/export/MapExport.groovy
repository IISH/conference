package org.iisg.eca.export

import groovy.transform.CompileStatic

/**
 * An abstract implementation of the <code>Export</code> interface
 */
@CompileStatic
abstract class MapExport implements Export {
    protected List<String> columns
    protected List<Map> results
    protected String title
    protected List<String> columnNames

    /**
     * Sets up the object with the required parameters
     * @param columns A list of columns to export
     * @param results A list of results to export
     * @param title The title of the resulting file
     * @param columnNames The names of the columns
     */
    MapExport(List<String> columns, List<Map> results, String title, List<String> columnNames) {
        this.columns = columns
        this.results = results
        this.title = title
        this.columnNames = columnNames
    }

    /**
     * Returns the results
     * @return The results
     */
    List<Map> getResults() {
        return results
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
