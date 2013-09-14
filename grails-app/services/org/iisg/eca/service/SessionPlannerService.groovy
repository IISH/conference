package org.iisg.eca.service

import org.iisg.eca.utils.TimeSlot

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.Room
import org.iisg.eca.domain.User
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.Equipment
import org.iisg.eca.domain.SessionState
import org.iisg.eca.domain.SessionDateTime
import org.iisg.eca.domain.SessionParticipant
import org.iisg.eca.domain.SessionRoomDateTime
import org.iisg.eca.domain.RoomSessionDateTimeEquipment

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
        SessionParticipant.disableHibernateFilter('hideDeleted')

        List<Session> result = (List<Session>) SessionParticipant.executeQuery('''
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

        SessionParticipant.enableHibernateFilter('hideDeleted')
        result
    }

    /**
     * Returns a map with all the sessions and a list of sessions that have the same participant(s) scheduled at the same time
     * @return A map of sessions
     */
    LinkedHashMap<Session, List<Session>> getSessionConflicts() {
        LinkedHashMap<Session, List<Session>> sessionMap = new LinkedHashMap<Session, List<Session>>()
        Map<Long, Session> userSessionMap = new HashMap<Long, Session>()        
        
        Session.executeQuery('''
            SELECT s, u
            FROM Session AS s
            INNER JOIN s.sessionRoomDateTime AS srdt
            INNER JOIN s.sessionParticipants AS sp
            INNER JOIN sp.user AS u 
            INNER JOIN srdt.room AS r
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
            ORDER BY r.roomNumber DESC, sdt.indexNumber DESC
        ''', [dateId: pageInformation.date.id]).each { sessionAndUser -> 
            Session session = sessionAndUser[0]
            User user = sessionAndUser[1]
            
            if (userSessionMap.containsKey(user.id)) {
                Session otherSession = userSessionMap.get(user.id)
                List<Session> sessions = sessionMap.get(otherSession)
                if (!sessions.contains(session)) {
                    sessions.add(session)
                }
            }
            else {
                userSessionMap.put(user.id, session)
                if (!sessionMap.containsKey(session)) {
                    sessionMap.put(session, new ArrayList<Session>())
                }
            }
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
     * @return Sessions ordered by the room code and then by date/time they are planned in
     */
    List<Session> getSessionsWithNotPresentParticipants() {
        Session.executeQuery(''' 
            SELECT DISTINCT s
            FROM Session AS s
            INNER JOIN s.sessionRoomDateTime AS srdt
            INNER JOIN s.sessionParticipants AS sp
            INNER JOIN srdt.room AS r
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
            ORDER BY r.roomNumber DESC, sdt.indexNumber DESC
        ''', [dateId: pageInformation.date.id])
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

        times.remove(null)
        times.unique()
    }

    /**
     * Returns a list of the current schedule of the current event date
     * @return A list with timeslots, every timeslot lists the equipment available and session planned
     * for a specific room, date and time of the current event date
     */
    List<TimeSlot> getSchedule() {
        // First collect all the necessary information, retrieve all rooms, dates and times...
        List<Object[]> roomDateTimes = (List<Object[]>) Room.executeQuery('''
            SELECT r, dt
            FROM Room AS r, SessionDateTime AS dt
            WHERE dt.day IN :days
            ORDER BY r.roomNumber, dt.indexNumber''', [days: Day.list()])
        
        // ... then retrieve all equipment available in the rooms at specific dates and times
        List<Object[]> equipment = (List<Object[]>) RoomSessionDateTimeEquipment.executeQuery('''
            SELECT rsdte.room.id, rsdte.sessionDateTime.id, rsdte.equipment
            FROM RoomSessionDateTimeEquipment AS rsdte
            ORDER BY rsdte.room.roomNumber, rsdte.sessionDateTime.indexNumber, rsdte.equipment.code
        ''')

        // ... then retrieve all sessions already scheduled in the rooms at specific dates and times
        List<Object[]> sessions = (List<Object[]>) SessionRoomDateTime.executeQuery('''
            SELECT srdt.room.id, srdt.sessionDateTime.id, srdt.session
            FROM SessionRoomDateTime AS srdt
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
}
