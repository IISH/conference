package org.iisg.eca.domain

/**
 * Domain class of table holding all possible participant states
 */
class ParticipantState extends EventDomain {
    static final long NEW_PARTICIPANT = 0L
    static final long PARTICIPANT_DATA_CHECKED = 1L
    static final long PARTICIPANT = 2L
    static final long WILL_BE_REMOVED = 3L
    static final long REMOVED_CANCELLED = 4L
    static final long REMOVED_DOUBLE_ENTRY = 5L
    static final long NO_SHOW = 6L
    static final long UNCLEAR = 7L
    static final long PARTICIPANT_DID_NOT_FINISH_REGISTRATION = 999L

    String state

    static hasMany = [participantDates: ParticipantDate]

    static constraints = {
        state   blank: false,   maxSize: 100
    }

    static mapping = {
        table 'participant_states'
        version false
        cache true

        id      column: 'participant_state_id'
        state   column: 'participant_state'
    }

    @Override
    String toString() {
        state
    }
}
