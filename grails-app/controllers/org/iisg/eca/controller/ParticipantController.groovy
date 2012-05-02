package org.iisg.eca.controller

import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.User
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Equipment
import org.iisg.eca.domain.Extra
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.web.multipart.commons.CommonsMultipartFile
import java.sql.Blob

class ParticipantController {
    /**
     * Information about the current page
     */
    def pageInformation

    /**
     * Service taking care of participant information
     */
    def participantService

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
                                        states:         participantService.getParticipantCounts(params)])
    }

    def show() {
        User user = User.get(params.id)

         if (!user) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'user.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
         }

        if (request.get) {
            ParticipantDate participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)
            if (participant) {
                render(view: "form", model: [user: user, participant: participant])
            }
            else {
                render(view: "form", model: [user: user, participant: null])
            }
        }
        else if (request.post) {
            ParticipantDate participant
            if (user) {
                participant = ParticipantDate.findByUser(user)
            }
            else {
                participant = new ParticipantDate(user: user)
            }

            bindData(participant, params, [include: ['invitationLetter', 'invitationLetterSent', 'lowerFeeRequested',
                    'lowerFeeAnswered', 'state', 'feeState']], "ParticipantDate")
            bindData(user, params, [include: ['title', 'firstName', 'lastName', 'gender', 'organisation',
                    'department', 'email', 'address', 'city', 'country', 'phone', 'mobile', 'extraInfo']], "User")

            participant.extras.clear()
            params."ParticipantDate.extras".each { extraId ->
                participant.addToExtras(Extra.get(extraId))
            }

            int i = 0
            while (params["Paper_${i}"]) {
                Paper paper = user.papers.find { it.id == params.long("Paper_${i}.id") }
                if (paper) {
                    bindData(paper, params, [include: ['title', 'abstr', 'coAuthors', 'state', 'comment',
                            'sessionProposal', 'proposalDescription', 'equipmentComment']], "Paper_${i}")

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

            if (!participant.save() || !user.save()) {
                render(view: "form", model: [user: user, participant: participant])
                return
            }
            else {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label'), user.id])
                redirect(uri: eca.createLink(action: 'list', noBase: true))
            }
        }
    }
}
