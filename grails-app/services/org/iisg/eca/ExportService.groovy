package org.iisg.eca

/**
 * Service responsible for exporting the results from the database to another format
 */
class ExportService {
    private static final String XML = "xml"
    private static final String CSV = "csv"
    private static final String XLS = "xls"

    /**
     * Parses the results based on the format requested
     * @param format The format requested, either xml, csv or xls
     * @param response The response to write the obtained file to
     * @param pageElement The element of the page in question
     * @param fileName If specified, the name of the resulting file, defaults to 'Export'
     * @param seperator If specified, seperates the data in the case of a csv with this character
     */
    def void getPage(format, response, pageElement, seperator=',', fileName='Export') {
        def parser

        switch (format.toLowerCase()) {
            case XML:
                parser = new XmlParser(pageElement, fileName)
                break
            case CSV:
                parser = new CsvParser(pageElement, fileName)
                parser.setSeperator(seperator)
                break
            case XLS:
                parser = new XlsParser(pageElement, fileName)
                break
        }

        // Parse the results and write it to the response
        response.setContentType(parser.contentType)
        response.setHeader("Content-disposition", "attachment;filename=${fileName}.${format.toLowerCase()}")
        response.outputStream << parser.parse()
    }
}
