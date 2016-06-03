package org.iisg.eca.domain

import org.iisg.eca.filter.SoftDelete

/**
 * Domain class of table holding all extras for a particular event
 */
@SoftDelete
class Extra extends EventDateDomain {
    String title
    String extra
    String description
    String secondDescription
    BigDecimal amount = BigDecimal.ZERO
	boolean isFinalRegistration = true
    Integer maxSeats
	int sortOrder = 0
	boolean deleted

    static belongsTo = ParticipantDate
    static hasMany = [participantDates: ParticipantDate]

    static mapping = {
        table 'extras'
        version false
	    sort 'sortOrder'

        id                  column: 'extra_id'
        title               column: 'title'
        extra               column: 'extra'
        description         column: 'description'
        secondDescription   column: 'description_2nd',  type: 'text'
        amount              column: 'amount'
	    isFinalRegistration column: 'is_final_registration'
        maxSeats            column: 'max_seats'
	    sortOrder           column: 'sort_order'
	    deleted             column: 'deleted'

        participantDates    joinTable: 'participant_date_extra'
    }

    static constraints = {
        title               nullable: true, masSize: 60
        extra               blank: false,   maxSize: 50
        description         nullable: true, maxSize: 100
        secondDescription   nullable: true
        amount              min: BigDecimal.ZERO
        maxSeats            nullable: true, min: 1
    }

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'title',
            'extra',
            'description',
            'secondDescription',
            'amount',
		    'isFinalRegistration',
            'maxSeats',
		    'sortOrder'
    ]

    @Override
    String toString() {
        extra
    }
}
