package org.iisg.eca.domain

class FeeState extends DefaultDomain {
    Event event
    String name
    boolean isDefaultFee

    static belongsTo = Event
    static hasMany = [feeAmounts: FeeAmount]

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
    }

    static hibernateFilters = {
        eventFilter(condition: "event_id = (:eventId) or event_id = null", types: 'long')
    }

    @Override
    String toString() {
        name
    }
}
