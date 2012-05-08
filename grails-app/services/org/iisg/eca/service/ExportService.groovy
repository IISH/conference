package org.iisg.eca.service

import org.iisg.eca.domain.Paper

import org.iisg.eca.export.Export
import org.iisg.eca.export.XmlExport
import org.iisg.eca.export.XlsExport
import org.iisg.eca.export.CsvExport
import javax.servlet.http.HttpServletResponse

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
    void getPage(format, response, columns, results, seperator=',', fileName=pageInformation.page.toString()) {
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

    /**
     * Returns the uploaded file of the given paper
     * @param paper The paper file to return
     * @param response The response to write the obtained file to
     */
    void getPaper(Paper paper, HttpServletResponse response) {
        response.contentType = paper.contentType
        response.setHeader("Content-disposition", "attachment;filename=${paper.fileName}")
        response.outputStream << paper.file
    }
}
