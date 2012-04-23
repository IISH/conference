package org.iisg.eca.service

import org.iisg.eca.export.XmlExport
import org.iisg.eca.export.XlsExport
import org.iisg.eca.export.CsvExport
import org.iisg.eca.export.Export

/**
 * Service responsible for exporting the results from the database to another format
 */
class ExportService {
    def pageInformation

    private static final String XML = "xml"
    private static final String CSV = "csv"
    private static final String XLS = "xls"

    /**
     * Parses the results based on the format requested
     * @param format The format requested, either xml, csv or xls
     * @param response The response to write the obtained file to
     * @param columns The columns that will be exported
     * @param result A list of results
     * @param seperator If specified, seperates the data in the case of a csv with this character
     * @param fileName If specified, the name of the resulting file, defaults to the page name
     */
    def void getPage(format, response, columns, results, seperator=',', fileName=pageInformation.page.toString()) {
        Export export

        switch (format.toLowerCase()) {
            case XML:
                export = new XmlExport(columns, results, fileName)
                break
            case CSV:
                export = new CsvExport(columns, results, fileName)
                export.seperator = seperator
                break
            case XLS:
                export = new XlsExport(columns, results, fileName)
                break
        }

        // Parse the results and write it to the response
        response.contentType = export.contentType
        response.setHeader("Content-disposition", "attachment;filename=${fileName}.${format.toLowerCase()}")
        response.outputStream << export.parse()
    }
}