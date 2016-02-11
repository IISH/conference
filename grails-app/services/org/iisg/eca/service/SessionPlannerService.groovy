package org.iisg.eca.service

import org.hibernate.FetchMode
import org.hibernate.criterion.CriteriaSpecification
import org.iisg.eca.domain.*
import org.iisg.eca.utils.PlannedSession
import org.iisg.eca.utils.TimeSlot
import org.iisg.eca.utils.UserDateTime
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable

/**
 * Service responsible for session planning activities
 */
class SessionPlannerService {
    def pageInformation
    
    /**
     * Finds out all possible combinations of equipment for the current event date
     * @return A set which contains a list of all possible equipment combinations
     */
    Set<List<Equipment>> getEquipmentCombinations() {
        GroovyCollections.subsequences(Equipment.list())
    }

    /**
     * Returns all accepted sessions that are not scheduled yet
     * @return All accepted sessions that are not scheduled yet
     */
    List<Session> getUnscheduledSessions() {
        Session.executeQuery('''
            FROM Session AS s
            WHERE NOT EXISTS (
                FROM SessionRoomDateTime AS srdt
                WHERE srdt.session.id = s.id
            )
            AND state.id = :stateId
            ORDER BY s.code
        ''', [stateId: SessionState.SESSION_ACCEPTED])
    }

    /**
     * Returns all necessary equipment for the given session
     * @param session The session in question
     * @return A list of equipment necessary
     */
    List<Equipment> getEquipment(Session session) {
        (List<Equipment>) Session.executeQuery('''
            SELECT DISTINCT e
            FROM Session AS s
            JOIN s.papers AS p
            JOIN p.equipment AS e
            WHERE s.id = :sessionId
        ''', [sessionId: session.id])
    }
    
    /**
     * Returns all sessions where at least one of the equipment requirements 
     * is not present at the scheduled time and room
     * @return Sessions ordered by the room code and then by date/time they are planned in
     */
    List<Session> getSessionsWithEquipmentConflicts() {
        Session.executeQuery('''
            SELECT DISTINCT s
            FROM Session AS s
            INNER JOIN s.papers AS p
            INNER JOIN p.equipment AS e
            INNER JOIN s.sessionRoomDateTime AS srdt
            INNER JOIN srdt.room AS r
            INNER JOIN srdt.sessionDateTime AS sdt 
            WHERE NOT EXISTS (
                FROM Session AS s2
                INNER JOIN s2.sessionRoomDateTime AS srdt2
                INNER JOIN srdt2.sessionDateTime AS sdt
                INNER JOIN sdt.roomSessionDateTimeEquipment AS rsdte
                INNER JOIN rsdte.equipment AS e2
                WHERE srdt2.room.id = rsdte.room.id
                AND s2.date.id = :dateId
                AND s2.deleted = false
                AND s.id = s2.id
                AND e.id = e2.id
            )
            ORDER BY r.roomNumber DESC, sdt.indexNumber DESC
        ''', [dateId: pageInformation.date.id])
    }

    /**
     * Returns a list of sessions that have the same participants scheduled as the given session
     * @param session The session of which the participants should be checked
     * @return A list of sessions that have the same participants scheduled as the given one
     */
    List<Session> getSessionsWithSameParticipants(Session session) {
        (List<Session>) SessionParticipant.executeQuery('''
            SELECT DISTINCT sp.session
            FROM SessionParticipant AS sp
            WHERE EXISTS (
                FROM SessionParticipant AS sp2
                WHERE sp.user.id = sp2.user.id
                AND sp.session.id <> sp2.session.id
                AND sp2.session.id = :sessionId
            )
            AND sp.session.id <> :sessionId
        ''', [sessionId: session.id])
    }

    /**
     * Returns a map with all the users and a list of sessions that have that same user scheduled at the same time
     * @return A map of user and the date time of the conflict pointing to a list of sessions that are problematic
     */
    LinkedHashMap<UserDateTime, List<Session>> getSessionConflicts() {
        LinkedHashMap<UserDateTime, List<Session>> sessionMap = new LinkedHashMap<UserDateTime, List<Session>>()
        
        Session.executeQuery('''
            SELECT s, u, sdt
            FROM Session AS s
            INNER JOIN s.sessionRoomDateTime AS srdt
            INNER JOIN s.sessionParticipants AS sp
            INNER JOIN sp.user AS u
            INNER JOIN srdt.sessionDateTime AS sdt 
            WHERE EXISTS (
                FROM Session AS s2
                INNER JOIN s2.sessionRoomDateTime AS srdt2
                INNER JOIN s2.sessionParticipants AS sp2
                WHERE s2.date.id = :dateId
                AND s2.deleted = false
                AND s.id <> s2.id	
                AND srdt.sessionDateTime.id = srdt2.sessionDateTime.id
                AND sp.user.id = sp2.user.id
            )
            ORDER BY sdt.indexNumber DESC
        ''', [dateId: pageInformation.date.id]).each { conflict -> 
            Session session = conflict[0]
            User user = conflict[1]
            SessionDateTime dateTime = conflict[2]
            
            UserDateTime userDateTime = new UserDateTime(user, dateTime)
            List<Session> sessions = sessionMap.get(userDateTime, new ArrayList<Session>())
            sessions.add(session)
        }
        
        sessionMap
    }

    /**
     * Returns a list of time slots, where at least one of the participants from the given session is not present
     * @param session The session of which the participants should be checked
     * @return A list of time slots, where at least one of the participants from the given session is not present
     */
    List<SessionDateTime> getTimesParticipantsNotPresent(Session session) {
        List<Long> userIds = SessionParticipant.findAllBySession(session)*.user.id

        // If there are no participants, then there is no point in executing the query
        if (userIds.isEmpty()) {
            return []
        }

        (List<SessionDateTime>) SessionParticipant.executeQuery('''
            SELECT DISTINCT dt
            FROM ParticipantDate AS p
            INNER JOIN p.user AS u
            INNER JOIN u.dateTimesNotPresent AS dt
            WHERE u.id IN :userIds
            AND u.deleted = false
        ''', [userIds: userIds])
    }

    /**
     * Returns all the sessions where at least one of the participants 
     * is not present at the scheduled time
     * @return A map of the user with a conflict pointing to a list of sessions that are problematic
     */
    LinkedHashMap<User, List<Session>> getSessionsWithNotPresentParticipants() {
        LinkedHashMap<User, List<Session>> sessionMap = new LinkedHashMap<User, List<Session>>()
        
        Session.executeQuery(''' 
            SELECT DISTINCT s, u
            FROM Session AS s
            INNER JOIN s.sessionRoomDateTime AS srdt
            INNER JOIN s.sessionParticipants AS sp
            INNER JOIN sp.user AS u
            INNER JOIN srdt.sessionDateTime AS sdt 
            WHERE EXISTS (
                FROM ParticipantDate AS p
                INNER JOIN p.user AS u
                INNER JOIN u.dateTimesNotPresent AS dt
                WHERE p.date.id = :dateId
                AND p.deleted = false
                AND dt.id = srdt.sessionDateTime.id
                AND sp.user.id = u.id
            )
            ORDER BY sdt.indexNumber DESC
        ''', [dateId: pageInformation.date.id]).each { conflict -> 
            Session session = conflict[0]
            User user = conflict[1]
            
            List<Session> sessions = sessionMap.get(user, new ArrayList<Session>())
            sessions.add(session)    
        }
        
        sessionMap
    }

    /**
     * Returns a list of time slots that have to be blocked, as it is not possible to plan sessions at that time
     * @param session The session to be checked
     * @return A list of time slots
     */
    List<SessionDateTime> getTimesToBeBlocked(Session session) {
        List<SessionDateTime> times

        List<Session> sessions = getSessionsWithSameParticipants(session)
        times = sessions.collect { SessionRoomDateTime.findBySession(it)?.sessionDateTime }

        times.addAll(getTimesParticipantsNotPresent(session))

        times.removeAll(Collections.singleton(null))
        times.unique()
    }

    /**
     * Returns a list of the current schedule of the current event date
     * @return A list with timeslots, every timeslot lists the equipment available and session planned
     * for a specific room, date and time of the current event date
     */
    List<TimeSlot> getSchedule() {
        // Schedule is only possible with dates, check if dates are configured
        List<Day> days = Day.list()
        if (!days) {
            return null
        }

        // First collect all the necessary information, retrieve all rooms, dates and times...
        List<Object[]> roomDateTimes = (List<Object[]>) Room.executeQuery('''
            SELECT r, dt
            FROM Room AS r, SessionDateTime AS dt
            WHERE dt.day IN :days
            ORDER BY r.roomNumber, dt.indexNumber''', [days: days])
        
        // ... then retrieve all equipment available in the rooms at specific dates and times
        List<Object[]> equipment = (List<Object[]>) RoomSessionDateTimeEquipment.executeQuery('''
            SELECT rsdte.room.id, rsdte.sessionDateTime.id, rsdte.equipment
            FROM RoomSessionDateTimeEquipment AS rsdte
            INNER JOIN rsdte.room AS r
            INNER JOIN rsdte.sessionDateTime AS sdt
            INNER JOIN rsdte.equipment AS e
            WHERE r.deleted = false
            AND sdt.deleted = false
            AND e.deleted = false
            ORDER BY rsdte.room.roomNumber, rsdte.sessionDateTime.indexNumber, rsdte.equipment.code
        ''')

        // ... then retrieve all sessions already scheduled in the rooms at specific dates and times
        List<Object[]> sessions = (List<Object[]>) SessionRoomDateTime.executeQuery('''
            SELECT srdt.room.id, srdt.sessionDateTime.id, srdt.session
            FROM SessionRoomDateTime AS srdt
            INNER JOIN srdt.room AS r
            INNER JOIN srdt.sessionDateTime AS sdt
            INNER JOIN srdt.session AS s
            WHERE r.deleted = false
            AND sdt.deleted = false
            AND s.deleted = false
            ORDER BY srdt.room.roomNumber, srdt.sessionDateTime.indexNumber
        ''')

        // Now combine them all in one list
        List<TimeSlot> schedule = new ArrayList<TimeSlot>()
        roomDateTimes.each { roomDateTime ->
            // Get all equipment available for this room at this timeslot
            List<Equipment> equipmentList = new ArrayList<Equipment>()

            Iterator equipmentIterator = equipment.iterator()
            boolean noMoreEquipment = false

            while (equipmentIterator.hasNext() && !noMoreEquipment) {
                Object[] equip = equipmentIterator.next()
                if ((equip[0] == roomDateTime[0].id) && (equip[1] == roomDateTime[1].id)) {
                    equipmentList.add((Equipment) equip[2])
                    equipmentIterator.remove()
                }
                else {
                    noMoreEquipment = true
                }
            }

            // Get the session for this room at this timeslot if scheduled already
            Session session = null
            if (!sessions.isEmpty() && (sessions[0][0] == roomDateTime[0].id) && (sessions[0][1] == roomDateTime[1].id)) {
                session = (Session) sessions[0][2]
                sessions.remove(0)
            }

            // Now combine everything and add to the list
            schedule.add(new TimeSlot((Room) roomDateTime[0], (SessionDateTime) roomDateTime[1], session, equipmentList))
        }

        schedule
    }

    /**
     * Create the current planned schedule for API purposes
     * @param dateId Because of caching issues, the date id is also necessary
     * @param dayId The day to filter on
     * @param timeId The time to filter on
     * @param networkId The network to filter on
     * @param roomId The room to filter on
     * @param sessionId The session to filter on
     * @param participantId The favorite sessions of the participant to filter on
     * @param terms The search terms to filter on
     * @return The currently planned schedule
     */
    @Cacheable(value = "programme", condition = "#participantId == null")
    List<PlannedSession> getProgramme(Long dateId, Long dayId, Long timeId, Long networkId, Long roomId, Long sessionId,
                                      Long participantId, String terms) {
        // Start by querying
        def results = SessionRoomDateTime.createCriteria().listDistinct {
            // Create aliases first for joins
            createAlias('room', 'r')
            createAlias('sessionDateTime', 'sdt')
            createAlias('sdt.day', 'd')
            createAlias('session', 's')
            if (networkId || (terms?.trim()?.size() > 0)) {
                createAlias('s.networks', 'n')
                if (terms?.trim()?.size() > 1) {
                    createAlias('s.sessionParticipants', 'sp', CriteriaSpecification.LEFT_JOIN)
                    createAlias('sp.user', 'u', CriteriaSpecification.LEFT_JOIN)
                    createAlias('s.papers', 'p', CriteriaSpecification.LEFT_JOIN)
                }
            }
            if (participantId) {
                createAlias('s.participantsFavorite', 'pf')
            }

            // Also fetch the joined tables
            fetchMode('r', FetchMode.JOIN)
            fetchMode('sdt', FetchMode.JOIN)
            fetchMode('d', FetchMode.JOIN)
            fetchMode('s', FetchMode.JOIN)

            // Default filtering on joined domain classes
            eq('r.deleted', false)
            eq('r.date.id', dateId)
            eq('sdt.deleted', false)
            eq('sdt.date.id', dateId)
            eq('s.deleted', false)
            eq('s.date.id', dateId)
            if (networkId || (terms?.trim()?.size() > 0)) {
                eq('n.deleted', false)
                eq('n.date.id', dateId)
                if (terms?.trim()?.size() > 0) {
                    ne('sp.type.id', ParticipantType.CO_AUTHOR) // Co-authors are for internal use only
                    or {
                        isNull('u.id')
                        eq('u.deleted', false)
                    }
                    or {
                        isNull('p.id')
                        and {
                            eq('p.deleted', false)
                            eq('p.date.id', dateId)
                        }
                    }
                }
            }

            // Set ids, if provided with ids
            if (roomId) {
                eq('r.id', roomId)
            }
            if (dayId) {
                eq('d.id', dayId)
            }
            if (timeId) {
                eq('sdt.id', timeId)
            }
            if (networkId) {
                eq('n.id', networkId)
            }
            if (sessionId) {
                eq('s.id', sessionId)
            }
            if (participantId) {
                eq('pf.id', participantId)
            }

            // Filter using the terms
            if (terms?.trim()?.size() > 0) {
                String[] listOfTerms = terms.split()
                and {
                    listOfTerms.each { t ->
                        if (t.trim().size() > 0) {
                            or {
                                like('u.firstName', "%${t.trim()}%")
                                like('u.lastName', "%${t.trim()}%")
                                like('p.title', "%${t.trim()}%")
                                like('p.coAuthors', "%${t.trim()}%")
                                like('s.name', "%${t.trim()}%")
                            }
                        }
                    }
                }
            }

            // Now make sure the ordering is correct
            order('sdt.indexNumber', 'asc')
            order('r.roomNumber', 'asc')
        }

        // Query all other required values as well
        def networks = Network.list()
        def networksBySession = Network.withCriteria {
            if (sessionId) {
                sessions {
                    eq('id', sessionId)
                }
            }

            projections {
                property('id')
                sessions {
                    property('id')
                }
            }
        }

        def papers = Paper.withCriteria {
            createAlias('session', 's')
            fetchMode('s', FetchMode.JOIN)

            if (sessionId) {
                eq('s.id', sessionId)
            }
        }

        def sessionParticipants = SessionParticipant.withCriteria {
            createAlias('session', 's')
            createAlias('user', 'u')
            createAlias('type', 't')

            fetchMode('s', FetchMode.JOIN)
            fetchMode('u', FetchMode.JOIN)
            fetchMode('t', FetchMode.JOIN)

            if (sessionId) {
                eq('s.id', sessionId)
            }

            order('t.importance', 'desc')
            order('u.lastName', 'asc')
            order('u.firstName', 'asc')
        }

        // Now create PlannedSession classes out of the results
        List<PlannedSession> planning = new ArrayList<PlannedSession>()
        results.each { result ->
            PlannedSession plannedSession = new PlannedSession()

            plannedSession.roomId = result.room.id
            plannedSession.roomName = result.room.roomName
            plannedSession.roomNumber = result.room.roomNumber

            plannedSession.dayId = result.sessionDateTime.day.id
            plannedSession.day = result.sessionDateTime.day.day

            plannedSession.timeId = result.sessionDateTime.id
            plannedSession.indexNumber = result.sessionDateTime.indexNumber
            plannedSession.period = result.sessionDateTime.period

            plannedSession.sessionId = result.session.id
            plannedSession.sessionCode = result.session.code
            plannedSession.sessionName = result.session.name

            plannedSession.networks = networksBySession
                    .findAll { it[1] == result.session.id }
                    .collect {
                def network = networks.find { n -> n.id == it[0] }
                if (network.showOnline && !network.deleted) {
                    PlannedSession.Network plannedNetwork = new PlannedSession.Network()
                    plannedNetwork.networkId = network.id
                    plannedNetwork.networkName = network.name
                    return plannedNetwork
                }
            }

            plannedSession.participants = sessionParticipants
                    .findAll { it.session.id == result.session.id }
                    .collect { sp ->
                if (sp.type.withPaper) {
                    PlannedSession.ParticipantWithPaper participant = new PlannedSession.ParticipantWithPaper()
                    participant.typeId = sp.type.id
                    participant.type = sp.type.toString()
                    participant.participantName = sp.user.toString()

                    Paper paper = papers.find { (it.user.id == sp.user.id) && (it.session.id == result.session.id) }
                    if (paper) {
                        participant.paperId = paper.id
                        participant.paperName = paper.title
                        participant.coAuthors = paper.coAuthors
                        participant.hasDownload = paper.hasPaperFile()
                        participant.paperAbstract = paper.abstr

                        return participant
                    }
                }
                else {
                    PlannedSession.Participant participant = new PlannedSession.Participant()
                    participant.typeId = sp.type.id
                    participant.type = sp.type.toString()
                    participant.participantName = sp.user.toString()
                    return participant
                }
                return null
            } as ArrayList<PlannedSession.Participant>
            plannedSession.participants.removeAll(Collections.singleton(null))

            planning.add(plannedSession)
        }

        return planning
    }

    @CacheEvict(value = "programme", allEntries = true)
    void removeProgrammeFromCache() { }
}
