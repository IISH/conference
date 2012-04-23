package org.iisg.eca.domain

class FeeAmount extends EventDateDomain {
    FeeState feeState
    Date endDate
    int numDaysStart
    int numDaysEnd
    String numDays = (numDaysStart == numDaysEnd) ? numDaysStart : "${numDaysStart}-${numDaysEnd}"
    BigDecimal feeAmount

    static transients = ['numDays']
    static belongsTo = FeeState

    static constraints = {
        date            nullable: true
        numDaysStart    min: 1, validator: { val, object -> object.date.days.dayNumber.max() <= val }
        numDaysEnd      min: 1, validator: { val, object -> object.date.days.dayNumber.max() <= val }
        feeAmount       min: BigDecimal.ZERO
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
    }
}
