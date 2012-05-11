package org.iisg.eca.utils

import org.iisg.eca.domain.Room
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.Equipment
import org.iisg.eca.domain.SessionDateTime

/**
 * Utility class for grouping all important information of a specific timeslot together
 */
class TimeSlot {
    Room room
    SessionDateTime sessionDateTime
    Session session
    Set<Equipment> equipment

    TimeSlot(Room room, SessionDateTime sessionDateTime, Session session, Set<Equipment> equipment) {
        this.room = room
        this.sessionDateTime = sessionDateTime
        this.session = session
        this.equipment = equipment
    }

    Room getRoom() {
        room
    }

    SessionDateTime getSessionDateTime() {
        sessionDateTime
    }

    Session getSession() {
        session
    }

    Set<Equipment> getEquipment() {
        equipment
    }

    Integer equipmentCombinationIndex(Set<List<Equipment>> equipmentCombos) {
        if (!equipment) {
            return -1
        }
        equipmentCombos.findIndexOf { it.intersect(equipment).size() == equipment.size() }
    }
}
