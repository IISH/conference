package org.iisg.eca.service

import org.iisg.eca.utils.TimeSlot

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.Room
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.Equipment
import org.iisg.eca.domain.SessionDateTime
import org.iisg.eca.domain.SessionParticipant
import org.iisg.eca.domain.SessionRoomDateTime
import org.iisg.eca.domain.RoomSessionDateTimeEquipment

/**
 * Service responsible for session planning activities
 */
class SessionPlannerService {

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
            AND state.id = 2
            ORDER BY s.code
        ''')
    }

    /**
     * Returns all necessary equipment for the given session
     * @param session The session in question
     * @return A list of equipment necessary
     */
    List<Equipment> getEquipment(Session session) {
        (List<Equipment>) Session.executeQuery('''
            SELECT e
            FROM Session AS s
            JOIN s.papers AS p
            JOIN p.equipment AS e
            WHERE s.id = :sessionId
            GROUP BY e
        ''', [sessionId: session.id])
    }

    /**
     * Returns a list of sessions that have the same participants scheduled as the given session
     * @param session The session of which the participants should be checked
     * @return A list of sessions that have the same participants scheduled as the given one
     */
    List<Session> getSessionsWithSameParticipants(Session session) {
        SessionParticipant.disableHibernateFilter('hideDeleted')

        List<Session> result = (List<Session>) SessionParticipant.executeQuery('''
            SELECT sp.session
            FROM SessionParticipant AS sp
            WHERE EXISTS (
                FROM SessionParticipant AS sp2
                WHERE sp.user.id = sp2.user.id
                AND sp.session.id <> sp2.session.id
                AND sp2.session.id = :sessionId
            )
            GROUP BY sp.session
        ''', [sessionId: session.id])

        SessionParticipant.enableHibernateFilter('hideDeleted')
        result
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
            SELECT dt
            FROM ParticipantDate AS p
            INNER JOIN p.user AS u
            INNER JOIN u.dateTimesNotPresent AS dt
            WHERE u.id IN :userIds
            AND u.deleted = false
            GROUP BY dt
        ''', [userIds: userIds])
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
            ORDER BY r.roomNumber, dt.index''', [days: Day.list()])
        
        // ... then retrieve all equipment available in the rooms at specific dates and times
        List<Object[]> equipment = (List<Object[]>) RoomSessionDateTimeEquipment.executeQuery('''
            SELECT rsdte.room.id, rsdte.sessionDateTime.id, rsdte.equipment
            FROM RoomSessionDateTimeEquipment AS rsdte
            ORDER BY rsdte.room.roomNumber, rsdte.sessionDateTime.index, rsdte.equipment.code
        ''')

        // ... then retrieve all sessions already scheduled in the rooms at specific dates and times
        List<Object[]> sessions = (List<Object[]>) SessionRoomDateTime.executeQuery('''
            SELECT srdt.room.id, srdt.sessionDateTime.id, srdt.session
            FROM SessionRoomDateTime AS srdt
            ORDER BY srdt.room.roomNumber, srdt.sessionDateTime.index
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
