package org.iisg.eca.controller

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Extra
import org.iisg.eca.domain.Network
import org.iisg.eca.domain.Equipment
import org.iisg.eca.domain.PaperState
import org.iisg.eca.domain.Volunteering
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.ParticipantVolunteering

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

    def list() {
        Map participants = [:]
        if (params.filter) {
            participants = participantService.getParticipants(params)
        }

        render(view: "list", model: [   participants:   participants,
                                        alfabet:        participants.keySet(),
                                        states:         participantService.getParticipantCounts()])
    }

    def show() {
        User user = User.get(params.id)
        ParticipantDate participant = null

        if (!user) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'user.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        if (request.get) {
            participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)
        }
        else if (request.post) {
            bindData(user, params, [include: ['title', 'firstName', 'lastName', 'gender', 'organisation',
                    'department', 'email', 'address', 'city', 'country', 'phone', 'mobile', 'extraInfo']], "User")

            if (!participant && params['add-to-date']?.equals('add')) {
                participant = new ParticipantDate(user: user)

                participant.save()
                user.save()
            }
            else if (!participant) {
                if (user.save()) {
                    flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label'), user.id])
                    redirect(uri: eca.createLink(action: 'list', noBase: true))
                    return
                }
            }
            else {
                bindData(participant, params, [include: ['invitationLetter', 'invitationLetterSent', 'lowerFeeRequested',
                        'lowerFeeAnswered', 'state', 'feeState']], "ParticipantDate")

                participant.extras.clear()
                params."ParticipantDate.extras".each { extraId ->
                    participant.addToExtras(Extra.get(extraId))
                }

                int i = 0
                participant.participantVolunteering.clear()
                while (params["ParticipantVolunteering_${i}"]) {
                    ParticipantVolunteering pv = new ParticipantVolunteering()
                    bindData(pv, params, [include: ['volunteering', 'network']], "ParticipantVolunteering_${i}")
                    participant.addToParticipantVolunteering(pv)
                }

                i = 0
                while (params["Paper_${i}"]) {
                    Paper paper = user.papers.find { it.id == params.long("Paper_${i}.id") }
                    if (paper) {
                        bindData(paper, params, [include: ['title', 'abstr', 'coAuthors', 'state', 'comment',
                                'networkProposal', 'sessionProposal', 'proposalDescription',
                                'equipmentComment']], "Paper_${i}")

                        CommonsMultipartFile file = (CommonsMultipartFile) params["Paper_${i}.file"]
                        if (file) {
                            paper.fileSize = file.size
                            paper.contentType = file.contentType
                            paper.fileName = file.originalFilename
                            paper.file = file.bytes
                        }

                        paper.equipment.clear()
                        params["Paper_${i}.equipment"].each { equipmentId ->
                            paper.addToEquipment(Equipment.get(equipmentId))
                        }
                    }
                    i++
                }

                if (participant.save() && user.save()) {
                    flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label'), user.id])
                    redirect(uri: eca.createLink(action: 'list', noBase: true))
                    return
                }
            }
        }

        render(view: "form", model: [   user: user,
                                        participant: participant,
                                        volunteering: Volunteering.list(),
                                        networks: Network.list(),
                                        paperSates: PaperState.list(),
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
    }
}
