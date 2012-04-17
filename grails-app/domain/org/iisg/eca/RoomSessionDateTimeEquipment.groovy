package org.iisg.eca

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class RoomSessionDateTimeEquipment extends DefaultDomain implements Serializable {
    Room room
    SessionDateTime sessionDateTime
    Equipment equipment

    static belongsTo = [Room, SessionDateTime, Equipment]

    static mapping = {
        table 'room_sessiondatetime_equipment'
        id composite: ['room', 'sessionDateTime', 'equipment']
        version false

        room            column: 'room_id'
        sessionDateTime column: 'session_datetime_id'
        equipment       column: 'equipment_id'
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
