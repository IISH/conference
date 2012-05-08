package org.iisg.eca.controller

import grails.converters.JSON

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.ParticipantType
import org.iisg.eca.domain.ParticipantTypeRule
import org.iisg.eca.domain.SessionParticipant
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.PaperState
import org.hibernate.property.Setter
import org.iisg.eca.domain.Setting

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
        Session session = Session.findById(params.id)

        if (!session) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'session.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        def participants = SessionParticipant.findAllBySession(session).collect { [it, Paper.findAllByUserAndSession(it.user, session)] }
        def equipment = participantSessionService.getEquipmentForSession(session)
        render(view: "form", model: [   eventSession:   session,
                                        types:          ParticipantType.list(),
                                        participants:   participants,
                                        equipment:      equipment])
    }

    def addParticipant() {
        Map responseMap = null

        if (request.xhr && params.session_id && params.participant_id && params.type_id) {
            Session session = Session.findById(params.session_id)
            User user = User.get(params.participant_id)
            ParticipantDate participant = ParticipantDate.findWhere(user: user, date: pageInformation.date)
            ParticipantType type = ParticipantType.findById(params.type_id)
            Paper paper = null

            if (session && user && participant && type) {
                if (type?.type?.equalsIgnoreCase('author')) {
                    if ((Setting.getByProperty(Setting.MAX_PAPERS_PER_PERSON_PER_SESSION).value.toInteger() > 1) && params.paper_id) {
                        paper = Paper.findById(params.paper_id)
                    }
                    else {
                        paper = user.papers.find { it.date.id == pageInformation.date.id }
                    }

                    if (!paper || (paper.user.id != user.id)) {
                        responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ['Paper'])]
                        render responseMap as JSON
                        return
                    }
                }

                SessionParticipant sessionParticipant = new SessionParticipant(user: user, type: type)
                session.addToSessionParticipants(sessionParticipant)

                if (paper) {
                    session.addToPapers(paper)
                }

                if (session.save(flush: true)) {
                    def participants = SessionParticipant.findAllBySession(session).collect { [it, Paper.findAllByUserAndSession(it.user, session)] }
                    def equipment = participantSessionService.getEquipmentForSession(session)
                    responseMap = [success: true, participants: participants.collect {
                        [   it[0].user.id,
                            it[0].type.id,
                            it[1]*.id.join(','),
                            (it[0].type.type.equalsIgnoreCase('author')) ? "${it[0].toString()} (Paper(s): ${it[1]*.title.join(', ')})" : it[0].toString()]
                    }, equipment: equipment]
                }
                else {
                    responseMap = [success: false, message: session.errors.allErrors.collect { g.message(error: it) }]
                }
            }
            else {
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ['Participant'])]
            }
            
            render responseMap as JSON
        }
        else if (request.xhr) {
            responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ['Participant'])]
        }

        if (responseMap) {
            render responseMap as JSON
        }
    }
    
    def deleteParticipant() {
        if (request.xhr) {
            Map responseMap = null
            User user = User.get(params.user_id)
            ParticipantType type = ParticipantType.findById(params.type_id)
            Session session = Session.findById(params.session_id)
            SessionParticipant sessionParticipant = SessionParticipant.findWhere(user: user, type: type)
            List<Paper> papers =  []

            if (params.paper_ids) {
                papers = Paper.withCriteria {
                    and {
                        inList('id', params.paper_ids.trim().split(',').collect { it.toLong() })
                        eq('session.id', session.id)
                        eq('user.id', user.id)
                    }
                }
            }

            if (sessionParticipant) {
                papers.each { session.removeFromPapers(it) }
                sessionParticipant.deleted = true

                if (session.save(flush: true)) {
                    def participants = SessionParticipant.findAllBySession(session).collect { [it, Paper.findAllByUserAndSession(it.user, session)] }
                    def equipment = participantSessionService.getEquipmentForSession(session)
                    responseMap = [success: true, participants: participants.collect {
                        [   it[0].user.id,
                            it[0].type.id,
                            it[1]*.id.join(','),
                            (it[0].type.type.equalsIgnoreCase('author')) ? "${it[0].toString()} (Paper(s): ${it[1]*.title.join(', ')})" : it[0].toString()]
                    }, equipment: equipment]
                }
                else {
                    responseMap = [success: false, message: session.errors.allErrors.collect { g.message(error: it) }]
                }
            }
            else {
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ['Participant'])]
            }
            
            render responseMap as JSON
        }
    }

    def participants() {
        if (request.xhr) {
            List<User> participants = participantSessionService.allParticipants
            render participants.collect { user ->
                def papers = user.papers.findAll { (it.date.id == pageInformation.date.id) && (it.session == null) }
                [label: "${user.firstName} ${user.lastName}", value: user.id, papers: papers.collect { [label: it.title, value: it.id] }]
            } as JSON
        }
    }

    def participantsWithType() {
        if (request.xhr && params.type_id && params.session_id) {
            List<User> participants = new ArrayList<User>()
            ParticipantType selectedType = ParticipantType.findById(params.long('type_id'))

            ParticipantTypeRule.getRulesForParticipantType(selectedType).each { rule ->
                if (rule.firstType.id == selectedType.id) {
                    participants.addAll(participantSessionService.getParticipantsOfType(rule.secondType.id, params.long('session_id')))
                }
                else if (rule.secondType.id == selectedType.id) {
                    participants.addAll(participantSessionService.getParticipantsOfType(rule.firstType.id, params.long('session_id')))
                }
            }
            participants.addAll(participantSessionService.getParticipantsOfType(selectedType.id, params.long('session_id')))

            if (selectedType.type.equalsIgnoreCase('author')) {
                participants.addAll(participantSessionService.getParticipantsWithoutOpenPapers())
            }

            render participants.unique().collect { it.id } as JSON
        }
    }
}
