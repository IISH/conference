package org.iisg.eca.domain

import org.iisg.eca.filter.SoftDelete

/**
 * Domain class of table holding all fee states
 */
@SoftDelete
class FeeState extends EventDateDomain {
    static final long NO_FEE_SELECTED = 0L

    String name
    boolean isDefaultFee = false
	boolean isAccompanyingPersonFee = false
	boolean deleted = false
    
    static hasMany = [participantDates: ParticipantDate, feeAmounts: FeeAmount]

    static constraints = {
        name    blank: false, maxSize: 50
    }

    static mapping = {
        table 'fee_states'
        version false
        sort 'name': 'asc'

        id                      column: 'fee_state_id'
        name                    column: 'name'
        isDefaultFee            column: 'is_default_fee'
	    isAccompanyingPersonFee column: 'is_accompanying_person_fee'
	    deleted                 column: 'deleted'

		feeAmounts              sort: 'endDate', cascade: 'all-delete-orphan'
    }

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'name',
            'isDefaultFee',
		    'isAccompanyingPersonFee',
            'feeAmounts.id'
    ]

    static namedQueries = {
        sortedFeeStates {
            order('isDefaultFee', 'desc')
	        order('isAccompanyingPersonFee', 'desc')
            order('name', 'asc')
        }
    }

	static FeeState getDefaultFee() {
		withCriteria {
			eq('isDefaultFee', true)
		}.first()
	}

	static FeeState getAccompanyingPersonFee() {
		withCriteria {
			eq('isAccompanyingPersonFee', true)
		}.first()
	}

	@Override
    String toString() {
        name
    }
}
