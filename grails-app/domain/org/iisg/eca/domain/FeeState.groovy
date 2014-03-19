package org.iisg.eca.domain

/**
 * Domain class of table holding all fee states
 */
class FeeState extends EventDomain {
    static final long NO_FEE_SELECTED = 0L

    String name
    boolean isDefaultFee = false
	boolean deleted = false
    
    static hasMany = [participantDates: ParticipantDate, feeAmounts: FeeAmount]

    static constraints = {
        event   nullable: true
        name    blank: false, maxSize: 50
    }

    static mapping = {
        table 'fee_states'
        version false
        sort 'name': 'asc'

        id              column: 'fee_state_id'
        event           column: 'event_id'
        name            column: 'name'
        isDefaultFee    column: 'is_default_fee'
	    deleted         column: 'deleted'

        feeAmounts      sort: 'endDate', cascade: 'all-delete-orphan'
    }

	static hibernateFilters = {
		eventFilter(condition: '(event_id = :eventId OR event_id IS NULL)', types: 'long')
		hideDeleted(condition: 'deleted = 0', default: true)
	}

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'name',
            'isDefaultFee',
            'feeAmounts.id'
    ]

    static namedQueries = {
        sortedFeeStates {
            order('isDefaultFee', 'desc')
            order('name', 'asc')
        }
    }

	void softDelete() {
		deleted = true
	}

	@Override
    String toString() {
        name
    }
}
