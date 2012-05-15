package org.iisg.eca.service

import org.iisg.eca.domain.Room
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.Equipment
import org.iisg.eca.domain.SessionDateTime
import org.iisg.eca.domain.SessionRoomDateTime
import org.iisg.eca.domain.RoomSessionDateTimeEquipment

import org.iisg.eca.utils.TimeSlot
import org.iisg.eca.domain.User
import org.iisg.eca.domain.SessionParticipant
import org.iisg.eca.domain.DefaultDomain

class SessionPlannerService {
    /**
     * Finds out all possible combinations of equipment for the current event date
     * @return A set which contains a list of all possible equipment combinations
     */
    Set<List<Equipment>> getEquipmentCombinations() {
        (Set<List<Equipment>>) GroovyCollections.subsequences(Equipment.list(sort: 'equipment'))
    }

    List<Session> getUnscheduledSessions() {
        Session.executeQuery('''
            FROM Session AS s
            WHERE NOT EXISTS (
                FROM SessionRoomDateTime AS srdt
                WHERE srdt.session.id = s.id
            )
        ''')
    }

    List<Equipment> getEquipment(Session session) {
        (List<Equipment>) Session.executeQuery('''
            SELECT e
            FROM Session AS s
            JOIN s.papers AS p
            JOIN p.equipment AS e
            WHERE s.id = :sessionId
        ''', [sessionId: session.id])
    }

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
     * Returns a list of the current schedule of the current event date
     * @return A list with timeslots, every timeslot lists the equipment available and session planned
     * for a specific room, date and time of the current event date
     */
    List<TimeSlot> getSchedule() {
        // First collect all the necessary information, retrieve all rooms, dates and times...
        List<Object[]> roomDateTimes = (List<Object[]>) Room.executeQuery('''
            SELECT r, dt
            FROM Room AS r, SessionDateTime AS dt
            ORDER BY r.roomName, dt.index''')

        // ... then retrieve all equipment available in the rooms at specific dates and times
        List<Object[]> equipment = (List<Object[]>) RoomSessionDateTimeEquipment.executeQuery('''
            SELECT rsdte.room.id, rsdte.sessionDateTime.id, rsdte.equipment
            FROM RoomSessionDateTimeEquipment AS rsdte
            ORDER BY rsdte.room.roomName, rsdte.sessionDateTime.index
        ''')

        // ... then retrieve all sessions already scheduled in the rooms at specific dates and times
        List<Object[]> sessions = (List<Object[]>) SessionRoomDateTime.executeQuery('''
            SELECT srdt.room.id, srdt.sessionDateTime.id, srdt.session
            FROM SessionRoomDateTime AS srdt
            ORDER BY srdt.room.roomName, srdt.sessionDateTime.index
        ''')

        // Now combine them all in one list
        List<TimeSlot> schedule = new ArrayList<TimeSlot>()
        roomDateTimes.each { roomDateTime ->
            // Get all equipment available for this room at this timeslot
            Set<Equipment> equipmentSet = new HashSet<Equipment>()

            Iterator equipmentIterator = equipment.iterator()
            boolean noMoreEquipment = false

            while (equipmentIterator.hasNext() && !noMoreEquipment) {
                Object[] equip = equipmentIterator.next()
                if ((equip[0] == roomDateTime[0].id) && (equip[1] == roomDateTime[1].id)) {
                    equipmentSet.add((Equipment) equip[2])
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
            schedule.add(new TimeSlot((Room) roomDateTime[0], (SessionDateTime) roomDateTime[1], session, equipmentSet))
        }

        schedule
    }
}
