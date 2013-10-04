package org.iisg.eca.domain

/**
 * Domain class of table holding all fee states
 */
class FeeState extends EventDomain {
    static final long NO_FEE_SELECTED = 0L

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
        sort 'isDefaultFee': 'desc'

        id              column: 'fee_state_id'
        event           column: 'event_id'
        name            column: 'name'
        isDefaultFee    column: 'is_default_fee'

        feeAmounts      sort: 'endDate', cascade: 'all-delete-orphan'
    }

    @Override
    String toString() {
        name
    }
}
