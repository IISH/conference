package org.iisg.eca.controller

/**
 * Controller responsible for creating and exporting program books in XML format
 */
class ExportController {
    def bookExportService

    def index() { }

    def days() {
        response.contentType = "application/xml"
        response.setHeader("Content-disposition", "attachment;filename=days.xml")
        response.outputStream << bookExportService.daysXml
    }

    def concordance() {
        response.contentType = "application/xml"
        response.setHeader("Content-disposition", "attachment;filename=concordance.xml")
        response.outputStream << bookExportService.concordanceXml
    }
}
