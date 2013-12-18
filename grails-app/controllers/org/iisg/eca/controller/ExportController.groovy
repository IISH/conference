package org.iisg.eca.controller

import org.iisg.eca.export.Export

/**
 * Controller responsible for creating and exporting program books in XML format
 */
class ExportController {
    def bookExportService
    def badgeExportService

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

    def sessions() {
        response.contentType = "application/xml"
        response.setHeader("Content-disposition", "attachment;filename=sessions.xml")
        response.outputStream << bookExportService.sessionsXml
    }

    def badges() {
        Export badges = badgeExportService.getParticipantsExport()

        response.contentType = badges.contentType
        response.setHeader("Content-disposition", "attachment;filename=${badges.getTitle()}.${badges.getExtension().toLowerCase()}")
        response.outputStream << badges.parse()
    }
}
