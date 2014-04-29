package org.iisg.eca.service

import org.iisg.eca.domain.Paper
import org.iisg.eca.export.CsvDynamicPageExport
import org.iisg.eca.export.CsvMapExport
import org.iisg.eca.export.Export
import org.iisg.eca.export.XlsDynamicPageExport
import org.iisg.eca.export.XlsMapExport
import org.iisg.eca.export.XmlDynamicPageExport
import org.iisg.eca.export.XmlMapExport

import javax.servlet.http.HttpServletResponse

/**
 * Service responsible for exporting the results from the database to another format
 */
class ExportService {
    /**
     * Information about the page, like the page name
     */
    def pageInformation

    private static final String XML = "xml"
    private static final String XLS = "xls"
    private static final String CSV = "csv"

    /**
     * Parses the results based on the format requested
     * @param format The format requested, either xml, csv or xls
     * @param response The response to write the obtained file to
     * @param columns The columns that will be exported
     * @param results A list of results
     * @param separator If specified, separates the data in the case of a csv with this character
     * @param fileName If specified, the name of the resulting file, defaults to the page name
     */
    void getPage(format, response, columns, results, separator=',', fileName=pageInformation.page.toString()) {
        Export export = null

        // To which format should we export the data?
        switch (format.toLowerCase()) {
            case XML:
                export = new XmlDynamicPageExport(columns, results, fileName)
                break
            case XLS:
                export = new XlsDynamicPageExport(columns, results, fileName)
                break
            case CSV:
            default:
                export = new CsvDynamicPageExport(columns, results, fileName)
                export.separator = separator
        }

        // Parse the results and write it to the response
        response.contentType = export.contentType
        response.setHeader("Content-disposition", "attachment;filename=${fileName}.${format.toLowerCase()}")
        response.outputStream << export.parse()
    }

	/**
	 * Parses the results based on the format requested
	 * @param format The format requested, either xml, csv or xls
	 * @param response The response to write the obtained file to
	 * @param columns The columns that will be exported
	 * @param results A list of results
	 * @param title The title of the file
	 * @param columnNames Names of the columns
	 * @param separator If specified, separates the data in the case of a csv with this character
	 */
	void getSQLExport(String format, response, List<String> columns, List<Map> results, String title, List<String> columnNames, String separator=',') {
		Export export = null

		// To which format should we export the data?
		switch (format.toLowerCase()) {
			case XML:
				export = new XmlMapExport(columns, results, title, columnNames)
				break
			case XLS:
				export = new XlsMapExport(columns, results, title, columnNames)
				break
			case CSV:
			default:
				export = new CsvMapExport(columns, results, title, columnNames)
				export.separator = separator
		}

		// Parse the results and write it to the response
		response.contentType = export.contentType
		response.setHeader("Content-disposition", "attachment;filename=${title}.${format.toLowerCase()}")
		response.outputStream << export.parse()
	}

    /**
     * Returns the uploaded file of the given paper
     * @param paper The paper file to return
     * @param response The response to write the obtained file to
     * @param prepend The string that has to be prepended to the filename of the paper
     */
    void getPaper(Paper paper, HttpServletResponse response, String prepend = '') {
        response.contentType = paper.contentType
        response.setHeader("Content-disposition", "attachment;filename=${prepend + paper.fileName}")
        response.outputStream << paper.file
    }
}
