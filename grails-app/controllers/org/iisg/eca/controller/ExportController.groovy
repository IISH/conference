package org.iisg.eca.controller

import org.iisg.eca.export.Export

/**
 * Controller responsible for creating and exporting program books in XML format
 */
class ExportController {
	def dataSource
	def bookExportService
	def miscExportService

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

	def badgesPayed() {
		Export badges = miscExportService.getParticipantsExport(miscExportService.BADGES_PAYED)

		response.contentType = badges.contentType
		response.setHeader("Content-disposition",
				"attachment;filename=${badges.getTitle()}.${badges.getExtension().toLowerCase()}")
		response.outputStream << badges.parse()
	}

	def badgesNotPayed() {
		Export badges = miscExportService.getParticipantsExport(miscExportService.BADGES_NOT_PAYED)

		response.contentType = badges.contentType
		response.setHeader("Content-disposition",
				"attachment;filename=${badges.getTitle()}.${badges.getExtension().toLowerCase()}")
		response.outputStream << badges.parse()
	}

	def badgesUnconfirmed() {
		Export badges = miscExportService.getParticipantsExport(miscExportService.BADGES_UNCONFIRMED)

		response.contentType = badges.contentType
		response.setHeader("Content-disposition",
				"attachment;filename=${badges.getTitle()}.${badges.getExtension().toLowerCase()}")
		response.outputStream << badges.parse()
	}

	def programAtAGlance() {
		Export program = miscExportService.getProgramAtAGlanceExport()

		response.contentType = program.contentType
		response.setHeader("Content-disposition",
				"attachment;filename=${program.getTitle()}.${program.getExtension().toLowerCase()}")
		response.outputStream << program.parse()
	}

	def participants() {
		Export participants = miscExportService.getParticipantsWithFilterExport(params)

		response.contentType = participants.contentType
		response.setHeader("Content-disposition",
				"attachment;filename=${participants.getTitle()}.${participants.getExtension().toLowerCase()}")
		response.outputStream << participants.parse()
	}
}
