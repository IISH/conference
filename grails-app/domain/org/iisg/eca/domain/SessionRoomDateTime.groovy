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
        version false

        id              column: 'session_room_datetime_id'
        room            column: 'room_id',              fetch: 'join'
        sessionDateTime column: 'session_datetime_id',  fetch: 'join'
        session         column: 'session_id',           fetch: 'join'
    }

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'session.id',
            'room.roomName',
            'room.roomNumber',
            'sessionDateTime.indexNumber',
            'sessionDateTime.day',
            'sessionDateTime.period'
    ]

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

    @Override
    String toString() {
        "${session.code}, ${room.roomNumber}-${sessionDateTime.indexNumber}"
    }
}
