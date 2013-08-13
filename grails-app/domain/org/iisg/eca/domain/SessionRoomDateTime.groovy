package org.iisg.eca.domain

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class SessionRoomDateTime extends EventDateDomain implements Serializable {
    Room room
    SessionDateTime sessionDateTime
    Session session

    static belongsTo = [Room, SessionDateTime, Session]

    static mapping = {
        table 'session_room_datetime'
        id composite: ['room', 'sessionDateTime', 'session']
        version false

        room            column: 'room_id'
        sessionDateTime column: 'session_datetime_id'
        session         column: 'session_id'
    }

    Long getId() {
        "${room.id}${sessionDateTime.id}${session.id}".toLong()
    }

    @Override
    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append room
        builder.append sessionDateTime
        builder.append session
        builder.toHashCode()
    }

    @Override
    boolean equals(other) {
        if (other == null || !(other instanceof SessionRoomDateTime)) {
            return false
        }

        def builder = new EqualsBuilder()
        builder.append room, other.room
        builder.append sessionDateTime, other.sessionDateTime
        builder.append session, other.session
        builder.isEquals()
    }
}
