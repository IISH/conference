package org.iisg.eca.controller

import grails.converters.JSON

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.ParticipantType
import org.iisg.eca.domain.ParticipantTypeRule
import org.iisg.eca.domain.SessionParticipant
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.ParticipantDate

class SessionController {
    def pageInformation
    def participantSessionService

    def index() {
        redirect(action: 'list', params: params)
    }

    def show() {
        forward(controller: 'dynamicPage', action: 'get')
    }

    def list() {
        forward(controller: 'dynamicPage', action: 'get')
    }

    def create() {
        forward(controller: 'dynamicPage', action: 'getAndPost')
    }

    def edit() {
        Session session = Session.get(params.id)

        if (!session) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'session.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        def participants = SessionParticipant.findAllBySession(session)
        def equipment = participantSessionService.getEquipmentForSession(session)
        render(view: "form", model: [   eventSession:   session,
                                        types:          ParticipantType.list(),
                                        participants:   participants,
                                        equipment:      equipment])
    }

    def addParticipant() {
        if (request.xhr) {
            Map responseMap = null
            Session session = Session.findById(params.session_id)
            User user = User.get(params.participant_id)
            ParticipantDate participant = ParticipantDate.findWhere(user: user, date: pageInformation.date)
            ParticipantType type = ParticipantType.findById(params.type_id)
            Paper paper = null

            if (type?.type?.equalsIgnoreCase('author')) {
                paper = Paper.findById(params.paper_id)
                if (paper == null || paper.user.id != user.id) {
                    render([success: false, message: 'Could not find the paper!']) as JSON
                }
            }

            if (session && user && participant && type) {
                SessionParticipant sessionParticipant = new SessionParticipant(user: user, type: type)
                session.addToSessionParticipants(sessionParticipant)

                if (paper) {
                    session.addToPapers(paper)
                }


                if (session.save(flush: true)) {
                    def participants = SessionParticipant.findAllBySession(session)
                    def equipment = participantSessionService.getEquipmentForSession(session)
                    responseMap = [success: true, participants: participants.collect { [it.user.id, it.type.id, it.toString()] }, equipment: equipment]
                }
                else {
                    responseMap = [success: false, message: 'Failed to save!']
                }
            }
            else {
                responseMap = [success: false, message: 'Failed to load the necessary information!']
            }
            render responseMap as JSON
        }
    }

    def participants() {
        if (request.xhr) {
            List<User> participants = participantSessionService.allParticipants
            render participants.collect { user ->
                def papers = user.papers.findAll { it.date.id == pageInformation.date.id }
                [label: "${user.firstName} ${user.lastName}", value: user.id, papers: papers.collect { [label: it.title, value: it.id] }]
            } as JSON
        }
    }

    def participantsWithType() {
        if (request.xhr && params.type_id && params.session_id) {
            List<User> participants = new ArrayList<User>()
            ParticipantType selectedType = ParticipantType.get(params.long('type_id'))

            ParticipantTypeRule.getRulesForParticipantType(selectedType).each { rule ->
                if (rule.firstType.id == selectedType.id) {
                    participants += participantSessionService.getParticipantsOfType(rule.secondType.id, params.long('session_id'))
                }
                else if (rule.secondType.id == selectedType.id) {
                    participants += participantSessionService.getParticipantsOfType(rule.firstType.id, params.long('session_id'))
                }
            }
            participants += participantSessionService.getParticipantsOfType(selectedType.id, params.long('session_id'))

            render participants.unique().collect { user ->
                [label: "${user.firstName} ${user.lastName}", value: user.id]
            } as JSON
        }
    }
}
