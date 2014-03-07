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
    String substituteName

    static belongsTo = FeeState

    static constraints = {
        date            nullable: true
        numDaysStart    min: 1
        numDaysEnd      min: 1, validator: { val, obj ->
                            if (obj.numDaysEnd < obj.numDaysStart) {                            
                                ["feeAmount.validation.end.message"]
                            }
                        }
        feeAmount       min: BigDecimal.ZERO
        substituteName  nullable: true,         maxSize: 50
    }

    static mapping = {
        table 'fee_amounts'
        version false

        id              column: 'fee_amount_id'
        date            column: 'date_id'
        feeState        column: 'fee_state_id'
        endDate         column: 'end_date'
        numDaysStart    column: 'nr_of_days_start'
        numDaysEnd      column: 'nr_of_days_end'
        feeAmount       column: 'fee_amount'
        substituteName  column: 'substitute_name'
    }

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'feeState.id',
            'endDate',
            'numDaysStart',
            'numDaysEnd',
            'feeAmount',
            'substituteName'
    ]

    String getNumDays() {
        (numDaysStart == numDaysEnd) ? numDaysStart : "${numDaysStart} - ${numDaysEnd}"
    }
}
