package org.iisg.eca.domain

class ParticipantType extends EventDomain {
    String type

    static hasMany = [sessionParticipants: SessionParticipant]

    static mapping = {
        table 'participant_types'
        version false

        id      column: 'participant_type_id'
        type    column: 'type'
    }

    static constraints = {
        type    blank: false,   maxSize: 30
    }

    @Override
    String toString() {
        type
    }
}
