package org.iisg.eca.controller

import grails.converters.JSON

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.ParticipantType
import org.iisg.eca.domain.SessionParticipant
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.SessionDateTime
import org.iisg.eca.domain.Room
import org.iisg.eca.domain.Network
import org.iisg.eca.domain.Equipment
import org.iisg.eca.domain.SessionRoomDateTime

class SessionController {
    def pageInformation
    def participantSessionService
    def sessionPlannerService

    def index() {
        redirect(action: 'list', params: params)
    }

    def show() {
        if (!params.id) {
            flash.message = message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        Session session = Session.findById(params.id)
        if (!session) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'session.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        def participants = participantSessionService.getParticipantsForSession(session)
        def equipment = participantSessionService.getEquipmentForSession(session)
        render(view: "show", model: [   eventSession:   session,
                                        participants:   participants,
                                        equipment:      equipment])
    }

    def list() {
        forward(controller: 'dynamicPage', action: 'get')
    }

    def create() {
        forward(controller: 'dynamicPage', action: 'getAndPost')
    }

    /**
     * Shows the edit page, which allows users to add participants to a session
     * with a specific participant type (and paper)
     */
    def edit() {
        if (!params.id) {
            flash.message = message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }
        
        Session session = Session.findById(params.id)
        if (!session) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'session.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        if (request.post) {
            bindData(session, params, [include: ["code", "name", "comment", "enabeled"]], "Session")

            int i = 0
            while (params["Session_${i}"]) {
                bindData(session, params, [include: ['networks']], "Session_${i}")
                i++
            }

            if (session.save(flush: true)) {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'session.label')])
                redirect(uri: eca.createLink(action: 'list', noBase: true))
                return
            }
        }

        def participants = participantSessionService.getParticipantsForSession(session)
        def equipment = participantSessionService.getEquipmentForSession(session)
        render(view: "form", model: [   eventSession:   session,
                                        types:          ParticipantType.list(),
                                        participants:   participants,
                                        equipment:      equipment,
                                        networks:       Network.list()])
    }

    /**
     * Shows tha plan sessions page, which allows one to plan sessions at a specific timeslot
     */
    def planDrag() {
        [   equipment:              sessionPlannerService.equipmentCombinations,
            schedule:               sessionPlannerService.schedule,
            sessionsUnscheduled:    sessionPlannerService.unscheduledSessions,
            dateTimes:              SessionDateTime.list(),
            rooms:                  Room.list()]
    }

    /**
     * Shows tha plan sessions page, which allows one to plan sessions at a specific timeslot
     */
    def planClick() {
        [   equipment:              sessionPlannerService.equipmentCombinations,
            schedule:               sessionPlannerService.schedule,
            sessionsUnscheduled:    sessionPlannerService.unscheduledSessions,
            dateTimes:              SessionDateTime.list(),
            rooms:                  Room.list()]
    }

    /**
     * Adds the given participant with the given type to the given session if allowed
     * (AJAX call)
     */
    def addParticipant() {
        if (request.xhr) {
            Map responseMap = null

            if (params.session_id && params.participant_id && params.type_id) {
                Session session = Session.findById(params.session_id)
                User user = User.get(params.participant_id)
                ParticipantDate participant = ParticipantDate.findWhere(user: user, date: pageInformation.date)
                ParticipantType type = ParticipantType.findById(params.type_id)
                Paper paper = null

                if (session && user && participant && type) {
                    // Find out if the participant also needs to add a paper to the session
                    if (type.withPaper) {
                        // If only one paper is allowed, get that one
                        Integer maxPapers = Setting.getByProperty(Setting.MAX_PAPERS_PER_PERSON_PER_SESSION).value?.toInteger()
                        if ((maxPapers == null || maxPapers > 1) && params.paper_id) {
                            paper = Paper.findById(params.paper_id)
                        }
                        else {
                            paper = user.papers.find { it.date.id == pageInformation.date.id }
                        }

                        // No paper found, then we can't add this participant to the session
                        if (!paper || (paper.user.id != user.id)) {
                            responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: [g.message(code: 'paper.label')])]
                            render responseMap as JSON
                            return
                        }
                    }

                    // Check if the participant with this type is blacklisted for this session
                    if (!participantSessionService.getBlacklistForTypeInSession(session, type).contains(user)) {
                        SessionParticipant sessionParticipant = new SessionParticipant(user: user, type: type)
                        session.addToSessionParticipants(sessionParticipant)

                        if (paper) {
                            session.addToPapers(paper)
                        }

                        if (session.save(flush: true)) {
                            // Everything is fine, return all updated data to the client
                            def participants = participantSessionService.getParticipantsForSession(session)
                            def equipment = participantSessionService.getEquipmentForSession(session)
                            responseMap = [success: true, participants: participants.collect {
                                [   id:             it.participant.user.id,
                                    participant:    it.participant.user.toString(),
                                    state:          it.participant.state.toString(),
                                    types:          it.types.collect { pType ->
                                                        [id: pType.id, type:  pType.toString()]
                                                    },
                                    paper:          (it.paper) ? "${g.message(code: 'paper.label')}: ${it.paper?.toString()}" : ""]
                            }, equipment: equipment]
                        }
                        else {
                            responseMap = [success: false, message: session.errors.allErrors.collect { g.message(error: it) }]
                        }
                    }
                    else {
                        responseMap = [success:  false, message: g.message(code: 'default.not.allowed.message')]
                    }
                }
            }

            // If there is no responseMap defined yet, it can only mean the participant could not be found
            if (!responseMap) {
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: [g.message(code: 'participantdate.label')])]
            }

            render responseMap as JSON
        }
    }

    /**
     * Removes the given participant with the given type from the given session
     * (AJAX call)
     */
    def deleteParticipant() {
        if (request.xhr) {
            Map responseMap = null
            User user = User.get(params.user_id)
            ParticipantType type = ParticipantType.findById(params.type_id)
            Session session = Session.findById(params.session_id)
            SessionParticipant sessionParticipant = SessionParticipant.findWhere(user: user, type: type, session: session)

            if (sessionParticipant) {
                sessionParticipant.delete()

                if (type.withPaper) {
                    session.papers.find { it.user.id == user.id }?.session = null
                }

                if (session.save(flush: true)) {
                    def participants = participantSessionService.getParticipantsForSession(session)
                    def equipment = participantSessionService.getEquipmentForSession(session)
                    responseMap = [success: true, participants: participants.collect {
                        [   id:             it.participant.user.id,
                            participant:    it.participant.user.toString(),
                            state:          it.participant.state.toString(),
                            types:          it.types.collect { pType ->
                                                [id: pType.id, type:  pType.toString()]
                                            },
                            paper:          (it.paper) ? "${g.message(code: 'paper.label')}: ${it.paper?.toString()}" : ""]
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

    /**
     * Returns a list will all participants that have signed up for the current event date
     * (AJAX call)
     */
    def participants() {
        if (request.xhr) {
            List<User> participants = participantSessionService.allParticipants
            render participants.collect { user ->
                def papers = user.papers.findAll { (it.date.id == pageInformation.date.id) && (it.session == null) }
                [label: "${user.firstName} ${user.lastName}", value: user.id, papers: papers.collect { [label: it.title, value: it.id] }]
            } as JSON
        }
    }

    /**
     * Returns a blacklist for participants with a specific type in a specific session
     * (AJAX call)
     */
    def participantsWithType() {
        if (request.xhr && params.type_id && params.session_id) {
            ParticipantType selectedType = ParticipantType.findById(params.long('type_id'))
            Session session = Session.findById(params.long('session_id'))
            render participantSessionService.getBlacklistForTypeInSession(session, selectedType).collect { it.id } as JSON
        }
    }

    def possibilities() {
        if (request.xhr && params.session_id) {
            Map possibilitiesResponse = [:]
            Session session = Session.findById(params.long('session_id'))

            List<Equipment> equipment = sessionPlannerService.getEquipment(session)
            possibilitiesResponse.put('equipment', equipment.collect { it.id })

            List<Session> sessions = sessionPlannerService.getSessionsWithSameParticipants(session)
            List<Long> dateTimeIds = sessions.collect { SessionRoomDateTime.findBySession(it)?.sessionDateTime?.id }.unique()
            dateTimeIds.remove(null)
            possibilitiesResponse.put('date-times', dateTimeIds)

            render possibilitiesResponse as JSON
        }
    }

    def planSession() {
        if (request.xhr && params.session_id && params.room_id && params.date_time_id) {
            Map possibilitiesResponse = null
            Session session = Session.findById(params.long('session_id'))
            Room room = Room.findById(params.long('room_id'))
            SessionDateTime sessionDateTime = SessionDateTime.findById(params.long('date_time_id'))

            if (session && room && sessionDateTime) {
                List<SessionRoomDateTime> sessionRoomDateTimes = SessionRoomDateTime.findAllByRoomAndSessionDateTime(room, sessionDateTime)
                if (sessionRoomDateTimes && !sessionRoomDateTimes.isEmpty()) {
                    sessionRoomDateTimes.get(0).delete(flush: true)
                }

                SessionRoomDateTime sessionRoomDateTime = SessionRoomDateTime.findBySession(session)
                if (sessionRoomDateTime) {
                    sessionRoomDateTime.delete(flush: true)
                }

                sessionRoomDateTime = new SessionRoomDateTime(session: session, room: room, sessionDateTime: sessionDateTime)
                possibilitiesResponse = [success: (boolean) sessionRoomDateTime.save(flush: true)]
            }

            if (!possibilitiesResponse) {
                possibilitiesResponse = [success: false]
            }

            render possibilitiesResponse as JSON
        }
    }

    def returnSession() {
        if (request.xhr && params.session_id) {
            Session session = Session.findById(params.long('session_id'))
            SessionRoomDateTime sessionRoomDateTime = SessionRoomDateTime.findBySession(session)

            if (sessionRoomDateTime) {
                sessionRoomDateTime.delete(flush: true)
            }

            Map possibilitiesResponse = [success: (boolean) sessionRoomDateTime]
            render possibilitiesResponse as JSON
        }
    }

    /*def possibilitiesAndInfo() {
        if (request.xhr && params.session_id) {
            Map possibilitiesResponse = [:]
            Session session = Session.findById(params.long('session_id'))

            if (session) {
                possibilitiesResponse.put('success', true)
                possibilitiesResponse.put('code', session.code)
                possibilitiesResponse.put('name', session.name)
                possibilitiesResponse.put('comment', session.comment)
                possibilitiesResponse.put('participants', session.sessionParticipants.collect { it.toString() })
                possibilitiesResponse.put('equipment', sessionPlannerService.getEquipment(session).collect { it.toString() })

                List<Equipment> equipment = sessionPlannerService.getEquipment(session)
                possibilitiesResponse.put('equipment-ids', equipment.collect { it.id })

                List<Session> sessions = sessionPlannerService.getSessionsWithSameParticipants(session)
                List<Long> dateTimeIds = sessions.collect { SessionRoomDateTime.findBySession(it)?.sessionDateTime?.id }.unique()
                dateTimeIds.remove(null)
                possibilitiesResponse.put('date-times', dateTimeIds)
            }
            else {
                possibilitiesResponse = [success: false, message: 'Not found!']
            }

            render possibilitiesResponse as JSON
        }
    }         */

    def sessionInfo() {
        if (request.xhr && params.session_id) {
            Map possibilitiesResponse = [:]
            Session session = Session.findById(params.long('session_id'))

            if (session) {
                possibilitiesResponse.put('success', true)
                possibilitiesResponse.put('code', session.code)
                possibilitiesResponse.put('name', session.name)
                possibilitiesResponse.put('comment', session.comment)
                possibilitiesResponse.put('participants', session.sessionParticipants.collect { it.toString() })
                possibilitiesResponse.put('equipment', sessionPlannerService.getEquipment(session).collect { it.toString() })
            }
            else {
                possibilitiesResponse = [success: false, message: 'Not found!']
            }

            render possibilitiesResponse as JSON
        }
    }

    def roomInfo() {
        if (request.xhr && params.room_id) {
            Map possibilitiesResponse = [:]
            Room room = Room.findById(params.long('room_id'))

            if (room) {
                possibilitiesResponse.put('success', true)
                possibilitiesResponse.put('number', room.roomNumber)
                possibilitiesResponse.put('name', room.roomName)
                possibilitiesResponse.put('seats', room.noOfSeats)
                possibilitiesResponse.put('comment', room.comment)
            }
            else {
                possibilitiesResponse = [success: false, message: 'Not found!']
            }

            render possibilitiesResponse as JSON
        }
    }
}
