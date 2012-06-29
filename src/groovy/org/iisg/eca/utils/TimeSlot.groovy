package org.iisg.eca.utils

import org.iisg.eca.domain.Room
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.Equipment
import org.iisg.eca.domain.SessionDateTime

/**
 * Utility class for grouping all important information of a specific time slot together
 */
class TimeSlot {
    Room room
    SessionDateTime sessionDateTime
    Session session
    List<Equipment> equipment

    /**
     * Creates a new object with all information of a specific time slot
     * @param room The room in question
     * @param sessionDateTime The date and time in question
     * @param session The session planned at that room and time
     * @param equipment The equipment available in that room and time
     */
    TimeSlot(Room room, SessionDateTime sessionDateTime, Session session, List<Equipment> equipment) {
        this.room = room
        this.sessionDateTime = sessionDateTime
        this.session = session
        this.equipment = equipment
    }

    /**
     * Returns the room of the time slot
     * @return The room
     */
    Room getRoom() {
        room
    }

    /**
     * Returns the date and time of the time slot
     * @return The date and time
     */
    SessionDateTime getSessionDateTime() {
        sessionDateTime
    }

    /**
     * Returns the session planned at this time slot
     * @return The session planned
     */
    Session getSession() {
        session
    }

    /**
     * Returns all equipment available in this room at this specific time and date
     * @return A list of all equipment available
     */
    List<Equipment> getEquipment() {
        equipment
    }

    /**
     * Returns the index from the given set of equipment combinations
     * that contains the exact same types of equipment
     * @param equipmentCombos A set of all possible equipment combinations
     * @return The index number
     */
    Integer equipmentCombinationIndex(Set<List<Equipment>> equipmentCombos) {
        if (!equipment) {
            return -1
        }

        equipmentCombos.findIndexOf {
            def intersect = it.intersect(equipment)
            def plus = it.plus(equipment)
            plus.removeAll(intersect)
            plus.isEmpty()
        }
    }
}
