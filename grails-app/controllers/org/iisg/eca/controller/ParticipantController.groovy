package org.iisg.eca.controller

import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.User
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Equipment
import org.iisg.eca.domain.Extra
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

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
        def participants = participantService.getParticipants(params)
        render(view: "list", model: [   page:           pageInformation.page,
                                        alfabet:        participants.keySet(),
                                        participants:   participants,
                                        states:         participantService.getParticipantCounts(params),
                                        dates:          participantService.datesList])
    }

    def show() {
        User user = User.get(params.id)
        GrailsParameterMap parameterMap = params

         if (!user) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'user.label'), params.id])
            redirect(uri: eca.createLink(previous: true, base: false))
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

            params."ParticipantDate.extras".each { extraId ->
                participant.addToExtras(Extra.get(String.toLong(extraId)))
            }

            i = 0
            while (params["Paper_${i}"]) {
                Paper paper = user.papers.find { it.id == params.long("Paper_${i}.id") }
                if (paper) {
                    bindData(paper, params, [include: ['title', 'abstr', 'file', 'coAuthors', 'state', 'comment',
                            'sessionProposal', 'proposalDescription', 'equipmentComment']], "Paper_${i}")
                    params["Paper_${i}.equipment"].each { equipmentId ->
                        paper.addToEquipment(Equipment.get(String.toLong(equipmentId)))
                    }
                }
            }

            if (!participant.save(flush: true)) {
                render(view: "form", model: [user: user, participant: participant])
                return
            }
            else {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'User.label'), user.id])
                redirect(uri: eca.createLink(action: 'list', base: false))
            }
        }
    }
}
