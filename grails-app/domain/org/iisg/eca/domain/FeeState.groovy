package org.iisg.eca.domain

class FeeState extends EventDomain {
    String name
    boolean isDefaultFee
    
    static hasMany = [participantDates: ParticipantDate, feeAmounts: FeeAmount]

    static constraints = {
        event   nullable: true
        name    blank: false, maxSize: 50
    }

    static mapping = {
        table 'fee_states'
        version false

        id              column: 'fee_state_id'
        event           column: 'event_id'
        name            column: 'name'
        isDefaultFee    column: 'is_default_fee'

        feeAmounts      sort: 'endDate'
    }

    @Override
    String toString() {
        name
    }
}
