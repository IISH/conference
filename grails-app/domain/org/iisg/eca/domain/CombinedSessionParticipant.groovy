package org.iisg.eca.domain

/**
 * Domain class of a view linking all session participant together
 */
class CombinedSessionParticipant extends EventDateDomain {
    User user
    Session session
    ParticipantType type
    SessionParticipant sessionParticipant
    User addedBy

    static belongsTo = [User, Session, ParticipantType, SessionParticipant]

    static mapping = {
        table 'vw_combined_session_participants'
        version false
        type sort: 'importance', order: 'desc'

        user                column: 'user_id',                  fetch: 'join'
        session             column: 'session_id',               fetch: 'join'
        type                column: 'participant_type_id',      fetch: 'join'
        sessionParticipant  column: 'session_participant_id'
        addedBy             column: 'added_by',                 fetch: 'join'
	}

    static apiActions = ['GET']

    static apiAllowed = [
            'user.id',
            'session.id',
            'type.id',
            'addedBy.id',
            'sessionParticipant.id',
            'user',
            'session',
            'type',
            'addedBy'
    ]

    @Override
    String toString() {
        "${user} (${type})"
    }
}
