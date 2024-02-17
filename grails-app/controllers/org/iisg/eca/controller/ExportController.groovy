package org.iisg.eca.controller

import org.iisg.eca.domain.Network
import org.iisg.eca.domain.ParticipantState
import org.iisg.eca.export.Export
import org.springframework.context.i18n.LocaleContextHolder

/**
 * Controller responsible for creating and exporting program books in XML format
 */
class ExportController {
    def dataSource
    def messageSource
    def pageInformation
    def bookExportService
    def miscExportService

    def index() {}

    def days() {
        response.contentType = "application/xml"
        response.setHeader("Content-disposition", "attachment;filename=\"days.xml\"")
        response.outputStream << bookExportService.daysXml
    }

    def concordance() {
        response.contentType = "application/xml"
        response.setHeader("Content-disposition", "attachment;filename=\"concordance.xml\"")
        response.outputStream << bookExportService.concordanceXml
    }

    def sessions() {
        response.contentType = "application/xml"
        response.setHeader("Content-disposition", "attachment;filename=\"sessions.xml\"")
        response.outputStream << bookExportService.getSessionsXml(false)
    }

    def sessionsOrderedByCode() {
        response.contentType = "application/xml"
        response.setHeader("Content-disposition", "attachment;filename=\"sessionsOrderedByCode.xml\"")
        response.outputStream << bookExportService.getSessionsOrderedByCodeXml(false)
    }

    def badgesPayed() {
        Export badges = miscExportService.getParticipantsExport(miscExportService.BADGES_PAYED)

        response.contentType = badges.contentType
        response.setHeader("Content-disposition",
                "attachment;filename=\"${badges.getTitle()}.${badges.getExtension().toLowerCase()}\"")
        response.outputStream << badges.parse()
    }

    def badgesNotPayed() {
        Export badges = miscExportService.getParticipantsExport(miscExportService.BADGES_NOT_PAYED)

        response.contentType = badges.contentType
        response.setHeader("Content-disposition",
                "attachment;filename=\"${badges.getTitle()}.${badges.getExtension().toLowerCase()}\"")
        response.outputStream << badges.parse()
    }

    def badgesUnconfirmed() {
        Export badges = miscExportService.getParticipantsExport(miscExportService.BADGES_UNCONFIRMED)

        response.contentType = badges.contentType
        response.setHeader("Content-disposition",
                "attachment;filename=\"${badges.getTitle()}.${badges.getExtension().toLowerCase()}\"")
        response.outputStream << badges.parse()
    }

    def programAtAGlance() {
        Export program = miscExportService.getProgramAtAGlanceExport()

        response.contentType = program.contentType
        response.setHeader("Content-disposition",
                "attachment;filename=\"${program.getTitle()}.${program.getExtension().toLowerCase()}\"")
        response.outputStream << program.parse()
    }

    def participantsWithParticipantState0() {
        Export participants = miscExportService.getParticipants([
                ParticipantState.NEW_PARTICIPANT
        ])

        response.contentType = participants.contentType
        response.setHeader("Content-disposition", "attachment;filename=\"participants_with_state_new.xls\"")
        response.outputStream << participants.parse()
    }

    def participantsWithParticipantState1() {
        Export participants = miscExportService.getParticipants([
                ParticipantState.PARTICIPANT_DATA_CHECKED
        ])

        response.contentType = participants.contentType
        response.setHeader("Content-disposition", "attachment;filename=\"participants_with_state_data_checked.xls\"")
        response.outputStream << participants.parse()
    }

    def participantsWithParticipantState2() {
        Export participants = miscExportService.getParticipants([
                ParticipantState.PARTICIPANT,
                ParticipantState.ONLINE_PARTICIPANT
        ])

        response.contentType = participants.contentType
        response.setHeader("Content-disposition", "attachment;filename=\"participants_with_state_participant.xls\"")
        response.outputStream << participants.parse()
    }

    def participantsWithParticipantState999() {
        Export participants = miscExportService.getParticipants([
                ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION
        ])

        response.contentType = participants.contentType
        response.setHeader("Content-disposition", "attachment;filename=\"participants_with_state_not_finished.xls\"")
        response.outputStream << participants.parse()
    }

    def participantsWithParticipantState012999() {
        Export participants = miscExportService.getParticipants([
                ParticipantState.NEW_PARTICIPANT,
                ParticipantState.PARTICIPANT_DATA_CHECKED,
                ParticipantState.PARTICIPANT,
                ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION,
                ParticipantState.ONLINE_PARTICIPANT
        ])

        response.contentType = participants.contentType
        response.setHeader("Content-disposition",
                "attachment;filename=\"participants_with_state_new_data_checked_participant_and_not_finished.xls\"")
        response.outputStream << participants.parse()
    }

    def participantsWithParticipantState012() {
        Export participants = miscExportService.getParticipants([
                ParticipantState.NEW_PARTICIPANT,
                ParticipantState.PARTICIPANT_DATA_CHECKED,
                ParticipantState.PARTICIPANT,
                ParticipantState.ONLINE_PARTICIPANT
        ])

        response.contentType = participants.contentType
        response.setHeader("Content-disposition",
                "attachment;filename=\"participants_with_state_new_data_checked_and_participant.xls\"")
        response.outputStream << participants.parse()
    }

    def participantsWithParticipantState12() {
        Export participants = miscExportService.getParticipants([
                ParticipantState.PARTICIPANT_DATA_CHECKED,
                ParticipantState.PARTICIPANT,
                ParticipantState.ONLINE_PARTICIPANT
        ])

        response.contentType = participants.contentType
        response.setHeader("Content-disposition",
                "attachment;filename=\"participants_with_state_data_checked_and_participant.xls\"")
        response.outputStream << participants.parse()
    }

    def participants() {
        Export participants = miscExportService.getParticipantsWithFilterExport(params)

        response.contentType = participants.contentType
        response.setHeader("Content-disposition",
                "attachment;filename=\"${participants.getTitle()}.${participants.getExtension().toLowerCase()}\"")
        response.outputStream << participants.parse()
    }

    def participantsInNetwork() {
        onNetworkExport { Network network ->
            String title = messageSource.getMessage('participantDate.multiple.label',
                    null, LocaleContextHolder.locale) + " ${network} ${pageInformation.date.getShortNameAndYear()}"
            return miscExportService.getParticipantsInNetworkExport(network, title, [
                    messageSource.getMessage('network.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.lastName.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.firstName.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.email.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.organisation.label', null, LocaleContextHolder.locale)
            ])
        }
    }

    def sessionPapersInNetwork() {
        onNetworkExport { Network network ->
            String title = messageSource.getMessage('session.label', null, LocaleContextHolder.locale) + " " +
                    messageSource.getMessage('paper.multiple.label', null, LocaleContextHolder.locale) +
                    " ${network} ${pageInformation.date.getShortNameAndYear()}"
            return miscExportService.getSessionPapersInNetworkExport(network, title, [
                    messageSource.getMessage('network.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.lastName.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.firstName.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.email.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.organisation.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('session.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('session.state.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('participantType.multiple.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('paper.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('paper.abstr.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('paper.state.label', null, LocaleContextHolder.locale)
            ])
        }
    }

    def sessionPapersInNetworkAccepted() {
        onNetworkExport { Network network ->
            String title = messageSource.getMessage('session.label', null, LocaleContextHolder.locale) + " " +
                    messageSource.getMessage('paper.multiple.label', null, LocaleContextHolder.locale) +
                    " ${network} ${pageInformation.date.getShortNameAndYear()}"
            return miscExportService.getSessionPapersInNetworkAcceptedExport(network, title, [
                    messageSource.getMessage('network.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.lastName.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.firstName.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.email.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.organisation.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('session.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('session.state.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('participantType.multiple.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('paper.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('paper.abstr.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('paper.state.label', null, LocaleContextHolder.locale)
            ])
        }
    }

    def individualPapersInNetwork() {
        onNetworkExport { Network network ->
            String title = messageSource.getMessage('paper.multiple.label', null, LocaleContextHolder.locale) +
                    " ${network} ${pageInformation.date.getShortNameAndYear()}"
            return miscExportService.getIndividualPapersInNetworkExport(network, title, [
                    messageSource.getMessage('network.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.lastName.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.firstName.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.email.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.organisation.label', null, LocaleContextHolder.locale),
                    '#',
                    messageSource.getMessage('paper.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('paper.coAuthors.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('paper.type.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('paper.state.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('paper.abstr.label', null, LocaleContextHolder.locale)
            ])
        }
    }

    def allPapersInNetwork() {
        onNetworkExport { Network network ->
            String title = messageSource.getMessage('paper.multiple.label', null, LocaleContextHolder.locale) +
                    " ${network} ${pageInformation.date.getShortNameAndYear()}"
            return miscExportService.getAllPapersInNetworkExport(network, title, [
                    messageSource.getMessage('network.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.lastName.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.firstName.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.email.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('user.organisation.label', null, LocaleContextHolder.locale),
                    '#',
                    messageSource.getMessage('paper.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('paper.coAuthors.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('paper.type.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('paper.state.label', null, LocaleContextHolder.locale),
                    messageSource.getMessage('paper.abstr.label', null, LocaleContextHolder.locale)
            ])
        }
    }

    private onNetworkExport(Closure onNetworkFound) {
        Long id = (params.containsKey('networkId') && params.networkId.toString().isLong()) ? params.long('networkId') : null
        if (id) {
            Network network = Network.findById(id)
            if (network && network.isShowOnline()) {
                Export export = onNetworkFound(network)

                response.contentType = export.contentType
                response.setHeader("Content-disposition",
                        "attachment;filename=\"${export.getTitle()}.${export.getExtension().toLowerCase()}\"")
                response.outputStream << export.parse()

                return
            }
        }
        response.sendError(400)
    }
}
