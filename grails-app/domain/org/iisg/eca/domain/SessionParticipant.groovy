package org.iisg.eca.domain

class SessionParticipant extends DefaultDomain {
    User user
    Session session
    ParticipantType type

    static belongsTo = [User, Session, ParticipantType]

    static mapping = {
        table 'session_participant'
        version false

        id      column: 'session_participant_id'
        user    column: 'user_id'
        session column: 'session_id'
        type    column: 'participant_type_id'
    }

    @Override
    String toString() {
        "${user} (${type})"
    }
}
