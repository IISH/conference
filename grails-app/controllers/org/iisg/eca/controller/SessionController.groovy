package org.iisg.eca.controller

import grails.converters.JSON

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.ParticipantType
import org.iisg.eca.domain.ParticipantTypeRule
import org.iisg.eca.domain.SessionParticipant

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
            Session session = Session.get(params.session_id)
            User user = User.get(params.participant_id)
            ParticipantType type = ParticipantType.get(params.type_id)

            if (session && user && type) {
                SessionParticipant sessionParticipant = new SessionParticipant(user: user, type: type)
                session.addToSessionParticipants(sessionParticipant)

                def participants = SessionParticipant.findAllBySession(session)
                def equipment = participantSessionService.getEquipmentForSession(session)

                if (!session.save(flush: true)) {
                    render([success: true, participants: participants, equipment: equipment]) as JSON
                }
                else {
                   render([success: false, message: 'Failed to save!']) as JSON
                }
            }
            else {
                render([success: false, message: 'Failed to load the necessary information!']) as JSON
            }
        }
    }

    def participants() {
        if (request.xhr) {
            def participants = null

            if (params.type_id) {
                ParticipantType selectedType = ParticipantType.get(params.long('type_id'))
                ParticipantTypeRule.getRulesForParticipantType(selectedType).each { rule ->
                    if (rule.firstType.id == selectedType.id) {
                        participants += participantSessionService.getParticipantsOfType(rule.secondType)
                    }
                    else if (rule.secondType.id == selectedType.id) {
                        participants += participantSessionService.getParticipantsOfType(rule.firstType)
                    }
                }
                participants += participantSessionService.getParticipantsOfType(selectedType)
            }
            else {
                participants = participantSessionService.allParticipants
            }

            if (participants) {
                render participants.unique().collect { [label: "${it[1]} ${it[2]}", value: it[0]] } as JSON
            }
        }
    }

    def participantswithtype() {
        if (request.xhr) {
            def participants = new ArrayList<Object[]>()

            if (params.type_id) {
                ParticipantType selectedType = ParticipantType.get(params.long('type_id'))
                ParticipantTypeRule.getRulesForParticipantType(selectedType).each { rule ->
                    if (rule.firstType.id == selectedType.id) {
                        participants += participantSessionService.getParticipantsOfType(rule.secondType)
                    }
                    else if (rule.secondType.id == selectedType.id) {
                        participants += participantSessionService.getParticipantsOfType(rule.firstType)
                    }
                }
                participants += participantSessionService.getParticipantsOfType(selectedType)
            }

            if (participants) {
                render participants.unique().collect { [label: "${it[1]} ${it[2]}", value: it[0]] } as JSON
            }
        }
    }
}
