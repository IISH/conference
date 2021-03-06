package org.iisg.eca.domain

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * Domain class of table holding all equipment for a room during a specific day and time of a session
 */
class RoomSessionDateTimeEquipment extends EventDateDomain implements Serializable {
    Room room
    SessionDateTime sessionDateTime
    Equipment equipment

    static belongsTo = [Room, SessionDateTime, Equipment]

    static mapping = {
        table 'room_sessiondatetime_equipment'
        id composite: ['room', 'sessionDateTime', 'equipment']
        version false

        room            column: 'room_id',              fetch: 'join'
        sessionDateTime column: 'session_datetime_id',  fetch: 'join'
        equipment       column: 'equipment_id',         fetch: 'join'
    }

    Long getId() {
        "${room.id}${sessionDateTime.id}${equipment.id}".toLong()
    }

    @Override
    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append room
        builder.append sessionDateTime
        builder.append equipment
        builder.toHashCode()
    }

    @Override
    boolean equals(other) {
        if (other == null || !(other instanceof RoomSessionDateTimeEquipment)) {
            return false
        }

        def builder = new EqualsBuilder()
        builder.append room, other.room
        builder.append sessionDateTime, other.sessionDateTime
        builder.append equipment, other.equipment
        builder.isEquals()
    }
}
