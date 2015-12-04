package org.iisg.eca.domain

/**
 * Domain class of table holding all fee amounts
 */
class FeeAmount extends EventDateDomain {
    FeeState feeState
    Date endDate
    int numDaysStart
    int numDaysEnd
    BigDecimal feeAmount = new BigDecimal(9999.99)
    BigDecimal feeAmountOnSite = new BigDecimal(9999.99)
    String substituteName

    static belongsTo = FeeState

    static constraints = {
        numDaysStart    min: 1
        numDaysEnd      min: 1, validator: { val, obj ->
                            if (obj.numDaysEnd < obj.numDaysStart) {                            
                                ["feeAmount.validation.end.message"]
                            }
                        }
        feeAmount       min: BigDecimal.ZERO
        feeAmountOnSite validator: { val, obj ->
                            if (obj.feeAmount.compareTo(obj.feeAmountOnSite) > 0) {
                                ["feeAmount.validation.onsite.message"]
                            }
                        }
        substituteName  nullable: true,         maxSize: 50
    }

    static mapping = {
        table 'fee_amounts'
        version false

        id              column: 'fee_amount_id'
        feeState        column: 'fee_state_id'
        endDate         column: 'end_date'
        numDaysStart    column: 'nr_of_days_start'
        numDaysEnd      column: 'nr_of_days_end'
        feeAmount       column: 'fee_amount'
        feeAmountOnSite column: 'fee_amount_on_site'
        substituteName  column: 'substitute_name'
    }

	static namedQueries = {
		getSortedFeeAmountsForState { FeeState state ->
			eq('feeState', state)
			order('endDate', 'asc')
			order('numDaysStart', 'asc')
			order('numDaysEnd', 'asc')
		}

		getFeeAmountForNrDays { FeeState state, int nrDays ->
			le('numDaysStart', nrDays)
			ge('numDaysEnd', nrDays)
			eq('feeState', state)
			order('endDate')
		}

		getFeeAmountForDate { FeeState state, Date date ->
			ge('endDate', date)
			eq('feeState', state)
			order('endDate')
		}

		getFeeAmountForDateAndNrDays { FeeState state, int nrDays, Date date ->
			getFeeAmountForNrDays(state, nrDays)
			getFeeAmountForDate(state, date)
		}
	}

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'feeState.id',
            'endDate',
            'numDaysStart',
            'numDaysEnd',
            'feeAmount',
            'feeAmountOnSite',
            'substituteName'
    ]

	static String getReadableFeeAmount(BigDecimal amount) {
		return amount.toString() + ' EUR'
	}

    String getNumDays() {
        (numDaysStart == numDaysEnd) ? numDaysStart : "${numDaysStart} - ${numDaysEnd}"
    }

	@Override
	String toString() {
		String name = feeState.name
		if (substituteName?.size() > 0) {
			name = substituteName
		}

		return "${name}: ${getReadableFeeAmount(feeAmount)}"
	}
}
