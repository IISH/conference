package org.iisg.eca.controller

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Extra
import org.iisg.eca.domain.Network
import org.iisg.eca.domain.FeeState
import org.iisg.eca.domain.Equipment
import org.iisg.eca.domain.PaperState
import org.iisg.eca.domain.Volunteering
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.SessionDateTime
import org.iisg.eca.domain.ParticipantState
import org.iisg.eca.domain.ParticipantVolunteering

import grails.converters.JSON
import grails.validation.ValidationException
import org.springframework.web.multipart.commons.CommonsMultipartFile

class ParticipantController {
    /**
     * Information about the current page
     */
    def pageInformation

    /**
     * Service taking care of participant information
     */
    def participantService

    /**
     * Service taking care of exporting the participants paper
     */
    def exportService

    def index() {
        redirect(action: "list", params: params)
    }

    def inventations() {
        forward(controller: "dynamicPage", action: "get")
    }

    def lowerFee() {
        forward(controller: "dynamicPage", action: "get")
    }

    def listAll() {
        forward(controller: "dynamicPage", action: "get")
    }

    def list() {
        Map participants = [:]
        if (params['filter-type']) {
            participants = participantService.getParticipants(params)
        }

        render(view: "list", model: [   participants:   participants,
                                        alfabet:        participants.keySet(),
                                        states:         participantService.getParticipantCounts()])
    }

    def show() {
        User user = User.get(params.id)

        if (!user) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'user.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        ParticipantDate participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)

        if (request.post) {
            try {
                bindData(user, params, [include: ['title', 'firstName', 'lastName', 'gender', 'organisation',
                        'department', 'email', 'address', 'city', 'country', 'phone', 'mobile', 'extraInfo']], "User")

                if (!participant && params['add-to-date']?.equals('add')) {
                    participant = new ParticipantDate(user: user, state: ParticipantState.get(0), feeState: FeeState.get(0))

                    participant.save(failOnError: true)
                    user.save(failOnError: true)
                }
                else if (!participant) {
                    user.save(failOnError: true)
                }
                else {
                    bindData(participant, params, [include: ['invitationLetter', 'invitationLetterSent', 'lowerFeeRequested',
                            'lowerFeeAnswered', 'state', 'feeState']], "ParticipantDate")

                    participant.extras.clear()
                    params."ParticipantDate.extras".each { extraId ->
                        participant.addToExtras(Extra.get(extraId))
                    }

                    user.dateTimesNotPresent.clear()
                    List<SessionDateTime> sessionDateTimes = SessionDateTime.list()
                    params.present.each { dateTimeId ->
                        sessionDateTimes.remove(sessionDateTimes.find { dateTimeId.isLong() && (it.id == dateTimeId.toLong()) })
                    }
                    user.dateTimesNotPresent.addAll(sessionDateTimes)

                    int i = 0
                    participant.participantVolunteering.clear()
                    participant.save(failOnError: true, flush: true)
                    while (params["ParticipantVolunteering_${i}"]) {
                        ParticipantVolunteering pv = new ParticipantVolunteering()
                        bindData(pv, params, [include: ['volunteering', 'network']], "ParticipantVolunteering_${i}")
                        participant.addToParticipantVolunteering(pv)
                        i++
                    }

                    String[] ids = params["to-be-deleted"].split(';')
                    ids.each { idToDelete ->
                        if (idToDelete.isLong()) {
                            Paper paperToDelete = Paper.findById(idToDelete.toLong())
                            paperToDelete?.softDelete()
                            paperToDelete?.save(failOnError: true)
                        }
                    }

                    i = 0
                    while (params["Paper_${i}"]) {
                        Paper paper = null

                        if (params["Paper_${i}.id"]?.isLong()) {
                            paper = user.papers.find { it.id == params.long("Paper_${i}.id") }
                        }

                        if (!paper) {
                            paper = new Paper(state: PaperState.get(0))
                            user.addToPapers(paper)
                        }

                        bindData(paper, params, [include: ['title', 'abstr', 'coAuthors', 'state', 'comment',
                                'networkProposal', 'sessionProposal', 'proposalDescription',
                                'equipmentComment']], "Paper_${i}")

                        CommonsMultipartFile file = (CommonsMultipartFile) params["Paper_${i}.file"]
                        if (file?.size > 0) {
                            paper.fileSize = file.size
                            paper.contentType = file.contentType
                            paper.fileName = file.originalFilename
                            paper.file = file.bytes
                        }

                        paper.equipment?.clear()
                        params["Paper_${i}.equipment"].each { equipmentId ->
                            paper.addToEquipment(Equipment.get(equipmentId))
                        }

                        paper.save(failOnError: true)
                        i++
                    }

                    user.save(failOnError: true)
                    participant.save(failOnError: true)
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'participantDate.label')])
                redirect(uri: eca.createLink(action: 'list', noBase: true))
                return
            }
            catch (ValidationException ve) {
                render(view: "form", model: [   user: user,
                                                participant: participant,
                                                papers: Paper.findAllByUser(user),
                                                volunteering: Volunteering.list(),
                                                networks: Network.list(),
                                                paperStates: PaperState.list(),
                                                equipmentList: Equipment.list()])
            }
        }

        render(view: "form", model: [   user: user,
                                        participant: participant,
                                        papers: Paper.findAllByUser(user),
                                        volunteering: Volunteering.list(),
                                        networks: Network.list(),
                                        paperStates: PaperState.list(),
                                        equipmentList: Equipment.list()])
    }

    def downloadPaper() {
        Paper paper = Paper.get(params.id)

        if (paper) {
            exportService.getPaper(paper, response)
        }
    }

    def removePaper() {
        Paper paper = Paper.get(params.long('paper-id'))

        if (paper) {
            paper.file = null
            paper.fileName = null
            paper.fileSize = null
            paper.contentType = null
            paper.save(flush: true)
        }
        
        Map returnMap = [success: true]
        render returnMap as JSON
    }
}
