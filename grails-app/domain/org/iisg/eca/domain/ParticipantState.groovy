package org.iisg.eca.domain

class ParticipantState extends EventDomain {
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
