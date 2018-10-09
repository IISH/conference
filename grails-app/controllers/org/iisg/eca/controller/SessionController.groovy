package org.iisg.eca.controller

import grails.converters.JSON

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.Page
import org.iisg.eca.domain.User
import org.iisg.eca.domain.Room
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.Network
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.Equipment
import org.iisg.eca.domain.DynamicPage
import org.iisg.eca.domain.SessionType
import org.iisg.eca.domain.SessionState
import org.iisg.eca.domain.SessionDateTime
import org.iisg.eca.domain.ParticipantType
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.SessionParticipant
import org.iisg.eca.domain.SessionRoomDateTime
import org.iisg.eca.domain.CombinedSessionParticipant

import org.iisg.eca.dynamic.DataContainer
import org.iisg.eca.dynamic.DynamicPageResults

import org.iisg.eca.utils.ParticipantSessionInfo

/**
 * Controller responsible for handling requests on sessions
 */
class SessionController {
    /**
     * Includes information about this page, such as the current event date
     */
    def pageInformation

    /**
     * The dynamic page service is responsible for dynamic page related actions
     */
    def dynamicPageService

    /**
     * The session planner service is responsible for actions related to the planning of sessions
     */
    def sessionPlannerService

    /**
     * Service taking care of participants and how they are added to a session
     */
    def participantSessionService

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
    }

    /**
     * Shows all data on a particular session
     */
    def show() {
        // We need an id, check for the id
        if (!params.id) {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        Session session = Session.findById(params.id)

        // We also need a session to be able to show something
        if (!session) {
            flash.error = true
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'session.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // If the user came to this page via the dynamic page listing all sessions, ask for the last results
        // Otherwise just return all listed sessions in its default order
        DynamicPage dynamicPage = dynamicPageService.getDynamicPage(Page.findByControllerAndAction(params.controller, 'list'))
        DynamicPageResults results = new DynamicPageResults((DataContainer) dynamicPage.elements.get(1), params)

        // Now we have the results, but we're only interested in the ids
        // (Actually, just the prev/next ids for the navigation section)
        List<Long> sessionIds = results.get()*.id

        // Let the participantSessionService come up with all participants and equipment for this session
        List<ParticipantSessionInfo> participants = participantSessionService.getParticipantsForSession(session)
        List<Object[]> equipment = participantSessionService.getEquipmentForSession(session)

        // Show the results to the user
        render(view: "show", model: [   eventSession:   session,
                                        networks:       Network.withCriteria { sessions { eq('id', session.id) } },
                                        participants:   participants,
                                        equipment:      equipment,
                                        sessionIds:     sessionIds])
    }

    /**
     * Shows a list of all sessions for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new session for the current event date
     */
    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows the edit page, which furthermore allows users to add participants to a session
     * with a specific participant type (and paper)
     */
    def edit() {
        // We need an id, check for the id
        if (!params.id) {
            flash.error = true
            flash.message = message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }
        
        Session session = Session.findById(params.id)

        // We also need a session to be able to show something
        if (!session) {
            flash.error = true
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'session.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all session related data
            bindData(session, params, [include: ["code", "name", "abstr", "comment", "state", "type"]], "Session")

            // Remove all networks from the session (one by one, cause we don't want to delete the networks themselves)
            List<Network> networks = []
            networks += session.networks
            networks.each { it.removeFromSessions(session) }
            session.save(flush: true)

            // Add all the networks send along to the session
            int i = 0
            while (params["Session_${i}"]) {
                if (params["Session_${i}.networks.id"]?.isLong()) {
                    session.addToNetworks(Network.findById(params.long("Session_${i}.networks.id")))
                }
                i++
            }

            // Save the session and redirect to the previous page if everything is ok
            if (session.save(flush: true)) {
                flash.message = g.message(code: 'default.updated.message', args: [g.message(code: 'session.label'), session.toString()])
                if (params['btn_save_close']) {
                    redirect(uri: eca.createLink(previous: true, noBase: true))
                    return
                }
            }
        }

        // Let the participantSessionService come up with all participants and equipment for this session
        List<ParticipantSessionInfo> participants = participantSessionService.getParticipantsForSession(session)
        List<Object[]> equipment = participantSessionService.getEquipmentForSession(session)

        // Show the results to the user
        render(view: "form", model: [   eventSession:   session,
                                        types:          ParticipantType.list(),
                                        participants:   participants,
                                        equipment:      equipment,
                                        networks:       Network.list(),
                                        sessionStates:  SessionState.list(),
                                        sessionTypes:   SessionType.list()])
    }

    def paperMismatch() {
        if (params.networkId?.isLong()) {
            String uri = eca.createLink(action: 'paperMismatch', noBase: true, id: params.networkId)
            params.remove('networkId')  
            
            redirect(uri: uri, params: params)
        }
        else {
            Network network = null
            List<Network> networks = Network.executeQuery('''
                FROM Network
                ORDER BY showOnline DESC, name ASC
            ''')
            
            if (params.id?.isLong()) {
                network = Network.findById(params.id)
            }
            else {
                network = networks.get(0)
                params.id = network.id
            }

            // Let the participantSessionService come up with all mismatches
            Map<Session, List<ParticipantSessionInfo>> sessions = participantSessionService.getParticipantsForSessionMismatches(network)

            render(view: "paperMismatch", model: [  network:    network,
                                                    networks:   networks,
                                                    networkIds: networks*.id,
                                                    sessions:   sessions])
        }
    }

    def warnings() {
        if (params.sessionId?.isLong()) {
            String uri = eca.createLink(action: 'warnings', noBase: true, id: params.sessionId)
            params.remove('sessionId')

            redirect(uri: uri, params: params)
        }
        else {
            Session session = null
            List<Session> sessions = Session.executeQuery('''
                FROM Session
                WHERE state.id = :stateId
                ORDER BY code, name
            ''', [stateId: SessionState.SESSION_ACCEPTED])

            if (params.id?.isLong()) {
                session = Session.findById(params.id.toLong())
            }
            else if (sessions.size() > 0) {
                session = sessions.get(0)
                params.id = session.id
            }
	        else {
	            flash.error = true
	            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'session.multiple.accepted.label')])
	            redirect(uri: eca.createLink(previous: true, noBase: true))
	            return
            }

            // Let the participantSessionService come up with all participants
            List<ParticipantSessionInfo> participants = participantSessionService.getParticipantsForSession(session)

            render(view: "warnings", model: [
                    session:        session,
                    sessions:       sessions,
                    sessionIds:     sessions*.id,
                    participants:   participants
            ])
        }
    }

    /**
     * Removes the session from the current event date
     */
    def delete() {
        // Of course we need an id of the session
        if (params.id) {
            Session session = Session.findById(params.id)
            session?.softDelete()

            // Try to remove the session, send back a success or failure message
            if (session?.save(flush: true)) {
                flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'session.label'), session.toString()])
            }
            else {
                flash.error = true
                flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'session.label'), session.toString()])
            }
        }
        else {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(controller: 'session', action: 'list', noBase: true))
    }

    /**
     * Shows tha plan sessions page, which allows one to plan sessions at a specific time slot
     */
    def plan() {
        [   equipment:              sessionPlannerService.equipmentCombinations,
            schedule:               sessionPlannerService.schedule,
            sessionsUnscheduled:    sessionPlannerService.unscheduledSessions,
            dateTimes:              SessionDateTime.findAllByDayInList(Day.list())]
    }

	/**
	 * Triggers to clear the program caches
	 */
	def clearProgrammeCache() {
		sessionPlannerService.removeProgrammeFromCache()
		render "Programme cache cleared."
	}
    
    /**
     * Changes the session state of the given session
     * (AJAX call)
     */
    def changeState() {
        // If this is an AJAX call, continue
        if (request.xhr) {
            Map responseMap = null
            
            // If we have a session id and state id, try to find the records for these ids
            if (params.session_id?.isLong() && params.state_id?.isLong()) {
                Session session = Session.findById(params.session_id)
                SessionState state = SessionState.findById(params.state_id)

                // Change the session state if they both exist
                if (session && state) {
                    session.state = state

                    // Save the session
                    if (session.save(flush: true)) {                        
                        // Everything is fine
                        Session.withNewSession { hSession ->
                            Session newSession = Session.findById(session.id)
                            List<ParticipantSessionInfo> participants = participantSessionService.getParticipantsForSession(newSession)
                            responseMap = [ success:        true,
                                            participants:   participantSessionService.getParticipantSessionInfoMap(participants)]
                        }
                    }
                    else {
                        responseMap = [success: false, message: session.errors.allErrors.collect { g.message(error: it) }]
                    }
                }
            }
            
            // If there is no responseMap defined yet, it can only mean the session or state could not be found
            if (!responseMap) {
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ["${g.message(code: 'session.label')}, ${g.message(code: 'session.state.label')}"])]
            }

            render responseMap as JSON
        }
    }

    /**
     * Adds the given participant with the given type to the given session if allowed
     * (AJAX call)
     */
    def addParticipant() {
        // If this is an AJAX call, continue
        if (request.xhr) {
            Map responseMap = null

            // If we have a session id, participant id and type id, try to find the records for these ids
            if (params.session_id?.isLong() && params.participant_id?.isLong() && params.type_id?.isLong()) {
                Session session = Session.findById(params.session_id)
                User user = User.get(params.participant_id)
                ParticipantDate participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)
                ParticipantType type = ParticipantType.findById(params.type_id)
                Paper paper = null

                // If we have found a session, user, participant and type, we can add the user to the session
                if (session && user && participant && type) {
                    // Find out if the participant also needs to add a paper to the session
                    if (type.withPaper) {
                        // If only one paper is allowed, get that one
                        Integer maxPapers = Setting.getSetting(Setting.MAX_PAPERS_PER_PERSON_PER_SESSION).value?.toInteger()
                        if ((maxPapers == null || maxPapers > 1) && params.paper_id) {
                            paper = Paper.findById(params.paper_id)
                        }
                        else {
                            paper = Paper.findByUser(user)
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
                        // Everything is ok thus far, add the participant to the session
                        SessionParticipant sessionParticipant = new SessionParticipant(user: user, type: type)
                        session.addToSessionParticipants(sessionParticipant)

                        // If we have a paper, make sure to add it to the session as well
                        if (paper) {
                            session.addToPapers(paper)
                        }

                        // Save the session
                        if (session.save(flush: true)) {
                            // Everything is fine, return all updated data to the client
                            List<ParticipantSessionInfo> participants = participantSessionService.getParticipantsForSession(session)
                            List<Object[]> equipment = participantSessionService.getEquipmentForSession(session)                            
                            responseMap = [ success:        true, 
                                            participants:   participantSessionService.getParticipantSessionInfoMap(participants), 
                                            equipment:      equipment]
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
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: [g.message(code: 'participantDate.label')])]
            }

            render responseMap as JSON
        }
    }

    /**
     * Removes the given participant with the given type from the given session
     * (AJAX call)
     */
    def deleteParticipant() {
        // If this is an AJAX call, continue
        if (request.xhr) {
            Map responseMap = null

             // If we have a session id, participant id and type id, try to find the records for these ids
            if (params.session_id?.isLong() && params.user_id?.isLong() && params.type_id?.isLong()) {
                User user = User.get(params.user_id)
                ParticipantType type = ParticipantType.findById(params.type_id)
                Session session = Session.findById(params.session_id)

                // Try to find the session participant
                SessionParticipant sessionParticipant = SessionParticipant.findWhere(user: user, type: type, session: session)

                // If found, delete it
                if (sessionParticipant) {
                    sessionParticipant.delete(flush: true)

                    // Look up the paper added to this session
                    Paper paper = Paper.findBySessionAndUser(session, user)

                    // Also look up if the participant is added to the session as a type with a paper
                    SessionParticipant isAddedWithPaper = SessionParticipant.findAllBySessionAndUser(session, user).find { it.type.withPaper }

                    // Make sure to remove the paper from the session as well
                    if (paper && (type.withPaper || !isAddedWithPaper)) {
                        paper.session = null
                        paper.save()
                    }

                    // Save the session
                    if (session.save(flush: true)) {
                        // Everything is fine, return all updated data to the client
                        List<ParticipantSessionInfo> participants = participantSessionService.getParticipantsForSession(session)
                        List<Object[]> equipment = participantSessionService.getEquipmentForSession(session)
                        responseMap = [ success:        true, 
                                        participants:   participantSessionService.getParticipantSessionInfoMap(participants), 
                                        equipment:      equipment]
                    }
                    else {
                        responseMap = [success: false, message: session.errors.allErrors.collect { g.message(error: it) }]
                    }
                }
                else {
                    responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: [g.message(code: 'participantDate.label')])]
                }
            }
            else {
                responseMap = [success:  false, message: g.message(code: 'default.not.allowed.message')]
            }
            
            render responseMap as JSON
        }
    }
    
    /**
     * Returns a list will all participants that have signed up for the current event date
     * (AJAX call)
     */
    def sessionParticipantsAutoComplete() {
        // If this is an AJAX call, continue
        if (request.xhr && params.type_id?.isLong() && params.session_id?.isLong()) {
            ParticipantType selectedType = ParticipantType.findById(params.long('type_id'))
            Session session = Session.findById(params.long('session_id'))
            def criteria = User.allParticipantsSoftState(pageInformation.date)

            String[] searchTerms = params.terms.toString().split()
            List<User> users = (List<User>) criteria {
                or {
                    for (String searchTerm : searchTerms) {
                        if (!searchTerm.isEmpty()) {
                            like('lastName', "%${searchTerm}%")
                            like('firstName', "%${searchTerm}%")
                        }
                    }
                }
            }

            Set<User> blacklist = participantSessionService.getBlacklistForTypeInSession(session, selectedType)
            users.removeAll(blacklist)

            // Return all participants and their paper, which are still not added to a session
            render users.collect { user ->
                if (selectedType.withPaper) {
                    List<Paper> papers = Paper.findAllByUserAndSessionIsNull(user)
                    [label: "${user.firstName} ${user.lastName}", value: user.id, papers: papers.collect { [label: it.title, value: it.id] }]
                }
                else {
                    [label: "${user.firstName} ${user.lastName}", value: user.id, papers: []]
                }
            } as JSON
        }
    }

    /**
     * Returns the equipment necessary to plan this session and the time slots that have to be blocked
     * (AJAX call)
     */
    def possibilities() {
        // If this is an AJAX call with a session id, continue
        if (request.xhr && params.session_id?.isLong()) {
            Map possibilitiesResponse = [:]
            Session session = Session.findById(params.long('session_id'))

            // Collect all the equipment for this session
            List<Equipment> equipment = sessionPlannerService.getEquipment(session)
            possibilitiesResponse.put('equipment', equipment*.id)

            // Collect all date/times of sessions with the same participants
            List<SessionDateTime> dateTimes = sessionPlannerService.getTimesToBeBlocked(session)
            possibilitiesResponse.put('date-times', dateTimes*.id)
            possibilitiesResponse.put('success',    true)

            render possibilitiesResponse as JSON
        }
    }

    /**
     * Returns all conflicts of planned sessions
     * (AJAX call)
     */
    def conflicts() {
        // If this is an AJAX call, continue
        if (request.xhr) {
            List<Map> noShow = new ArrayList<Map>()
            List<Map> alreadyPlanned = new ArrayList<Map>()
            List<Map> equipmentProblems = new ArrayList<Map>()

            sessionPlannerService.getSessionsWithNotPresentParticipants().each { user, conflictingSessions ->
                 noShow.add([
                        plannedUser:               user.toString(),
                        userUrl:                   eca.createLink(controller: 'participant', action: 'show', id: user.id),                         
                        conflictingSessions:       conflictingSessions.collect { it.sessionRoomDateTime.first().toString() },
                        conflictingSessionsUrls:   conflictingSessions.collect { eca.createLink(controller: 'session', action: 'show', id: it.id) },
                        text:                      g.message(code: 'session.noShow.label')
                 ])
            }

            sessionPlannerService.getSessionConflicts().each { userDateTime, conflictingSessions ->
                 alreadyPlanned.add([
                        plannedUser:               userDateTime.getUser().toString(),
                        userUrl:                   eca.createLink(controller: 'participant', action: 'show', id: userDateTime.getUser().id),
                        conflictingSessions:       conflictingSessions.collect { it.sessionRoomDateTime.first().toString() },
                        conflictingSessionsUrls:   conflictingSessions.collect { eca.createLink(controller: 'session', action: 'show', id: it.id) },
                        text:                      g.message(code: 'session.sessionConflict.label')
                 ])
            }

            sessionPlannerService.getSessionsWithEquipmentConflicts().each { session -> 
                 equipmentProblems.add([
                        plannedSession: session.sessionRoomDateTime.first().toString(),
                        sessionUrl:     eca.createLink(controller: 'session', action: 'show', id: session.id),
                        plannedRoom:    session.sessionRoomDateTime.first().room.toString(),
                        roomUrl:        eca.createLink(controller: 'room', action: 'show', id: session.sessionRoomDateTime.first().room.id),
                        text:           g.message(code: 'session.equipmentProblem.label')
                 ])
            }
             
            Map responseMap = [success: true, noShow: noShow, alreadyPlanned: alreadyPlanned, equipmentProblems: equipmentProblems]
            render responseMap as JSON
        }
    }

    /**
     * Plans a session in a specific room at a specific time slot
     * (AJAX call)
     */
    def planSession() {
        // If this is an AJAX call with session id, room id and date/time id, continue
        if (request.xhr && params.session_id?.isLong() && params.room_id?.isLong() && params.date_time_id?.isLong()) {
            Map possibilitiesResponse = null

            // Try to find all records for these ids
            Session session = Session.findById(params.long('session_id'))
            Room room = Room.findById(params.long('room_id'))
            SessionDateTime sessionDateTime = SessionDateTime.findByIdAndDayInList(params.long('date_time_id'), Day.list())

            // If found, try to plan the session
            if (session && room && sessionDateTime) {
                List<SessionRoomDateTime> sessionRoomDateTimes = SessionRoomDateTime.findAllByRoomAndSessionDateTime(room, sessionDateTime)

                // Something is already planned at that time slot, un plan it.
                if (sessionRoomDateTimes && !sessionRoomDateTimes.isEmpty()) {
                    sessionRoomDateTimes.get(0).delete(flush: true)
                }

                SessionRoomDateTime sessionRoomDateTime = SessionRoomDateTime.findBySession(session)

                // Session is already planned somewhere, un plan it
                if (sessionRoomDateTime) {
                    sessionRoomDateTime.delete(flush: true)
                }

                // Now, we can plan the session at the given time slot
                sessionRoomDateTime = new SessionRoomDateTime(session: session, room: room, sessionDateTime: sessionDateTime)
                possibilitiesResponse = [success: (boolean) sessionRoomDateTime.save(flush: true)]
            }

            if (!possibilitiesResponse) {
                possibilitiesResponse = [success: false]
            }

            render possibilitiesResponse as JSON
        }
    }

    /**
     * Un plan an session
     * (AJAX call)
     */
    def returnSession() {
        // If this is an AJAX call with session id, continue
        if (request.xhr && params.session_id?.isLong()) {
            Session session = Session.findById(params.long('session_id'))
            SessionRoomDateTime sessionRoomDateTime = SessionRoomDateTime.findBySession(session)

            // Un plan the session
            if (sessionRoomDateTime) {
                sessionRoomDateTime.delete(flush: true)
            }

            Map possibilitiesResponse = [success: (boolean) sessionRoomDateTime]
            render possibilitiesResponse as JSON
        }
    }

    /**
     * Return all information about the session in JSON format
     * (AJAX call)
     */
    def sessionInfo() {
        // If there is an AJAX call with session id, continue
        if (request.xhr && params.session_id?.isLong()) {
            Map possibilitiesResponse = [:]
            Session session = Session.findById(params.long('session_id'))

            // If the session is found, get all the info about the session
            if (session) {
                List<ParticipantSessionInfo> participants = participantSessionService.getParticipantsForSession(session)

                possibilitiesResponse.put('success',        true)
                possibilitiesResponse.put('code',           session.code)
                possibilitiesResponse.put('name',           session.name)
                possibilitiesResponse.put('state',          session.state.toString())
                possibilitiesResponse.put('comment',        session.comment)
                possibilitiesResponse.put('equipment',      sessionPlannerService.getEquipment(session).collect { it.toString() })
                possibilitiesResponse.put('participants',   participantSessionService.getParticipantSessionInfoMap(participants))

                // Find out what planning problems there are
                List<String> problems = []
                if (!session.plannedDateTime) {
                    Set<User> users = participants*.participant*.user as Set<User>

                    Map<SessionDateTime, Set<User>> dateTimesSameParticipants = new TreeMap<>()
                    List<Session> sessionsSameParticipants = sessionPlannerService.getSessionsWithSameParticipants(session)
                    sessionsSameParticipants.each { Session sessionsSameParticipant ->
                        sessionsSameParticipant.combinedSessionParticipants.each { CombinedSessionParticipant sessionParticipant ->
                            if (users.contains(sessionParticipant.user)) {
                                SessionDateTime plannedDateTime = sessionParticipant.session.plannedDateTime
                                if (plannedDateTime) {
                                    Set<User> sameUsers = dateTimesSameParticipants.get(plannedDateTime, new HashSet<User>())
                                    sameUsers.add(sessionParticipant.user)
                                    dateTimesSameParticipants.put(plannedDateTime, sameUsers)
                                }
                            }
                        }
                    }

                    problems += dateTimesSameParticipants.collect { SessionDateTime dateTime, Set<User> problemUsers ->
                        g.message(code: 'session.alreadyPlannedProblem.label', args: [dateTime, problemUsers.join(', ')])
                    }

                    Map<SessionDateTime, Set<User>> dateTimesParticipantNotPresent = new TreeMap<>()
                    users.each { User user ->
                        user.dateTimesNotPresent.each { SessionDateTime dateTime ->
                            Set<User> usersNotPresent = dateTimesParticipantNotPresent.get(dateTime, new HashSet<User>())
                            usersNotPresent.add(user)
                            dateTimesParticipantNotPresent.put(dateTime, usersNotPresent)
                        }
                    }

                    problems += dateTimesParticipantNotPresent.collect { SessionDateTime dateTime, Set<User> problemUsers ->
                        g.message(code: 'session.notPresentProblem.label', args: [dateTime, problemUsers.join(', ')])
                    }
                }
                possibilitiesResponse.put('problems', problems)
            }
            else {
                possibilitiesResponse = [success: false, message: g.message(code: 'default.not.found.message', args: [g.message(code: 'session.label')])]
            }

            render possibilitiesResponse as JSON
        }
    }

    /**
     * Return all information about the room in JSON format
     * (AJAX call)
     */
    def roomInfo() {
        // If there is an AJAX call with room id, continue
        if (request.xhr && params.room_id?.isLong()) {
            Map possibilitiesResponse = [:]
            Room room = Room.findById(params.long('room_id'))

            // If the room is found, get all the info about the room
            if (room) {
                possibilitiesResponse.put('success', true)
                possibilitiesResponse.put('number', room.roomNumber)
                possibilitiesResponse.put('name', room.roomName)
                possibilitiesResponse.put('seats', room.noOfSeats)
                possibilitiesResponse.put('comment', room.comment)
            }
            else {
                possibilitiesResponse = [success: false, message: g.message(code: 'default.not.found.message', args: [g.message(code: 'room.label')])]
            }

            render possibilitiesResponse as JSON
        }
    }
}
