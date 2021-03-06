package org.iisg.eca.domain

/**
 * Domain class of table holding all fee states
 */
class FeeState extends EventDateDomain {
    static final long NO_FEE_SELECTED = 0L

    String name
    boolean isDefaultFee = false
    boolean isStudentFee = false
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
        isStudentFee            column: 'is_student_fee'
	    isAccompanyingPersonFee column: 'is_accompanying_person_fee'
	    deleted                 column: 'deleted'

		feeAmounts              sort: 'endDate', cascade: 'all-delete-orphan'
    }

	static hibernateFilters = {
		dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
		hideDeleted(condition: 'deleted = 0', default: true)
	}

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'name',
            'isDefaultFee',
            'isStudentFee',
		    'isAccompanyingPersonFee',
            'feeAmounts.id'
    ]

    static namedQueries = {
        sortedFeeStates {
            order('isDefaultFee', 'desc')
            order('isStudentFee', 'desc')
	        order('isAccompanyingPersonFee', 'desc')
            order('name', 'asc')
        }
    }

	void softDelete() {
		deleted = true
	}

	static FeeState getDefaultFee() {
		withCriteria {
			eq('isDefaultFee', true)
		}.first()
	}

    static FeeState getStudentFee() {
        withCriteria {
            eq('isStudentFee', true)
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
