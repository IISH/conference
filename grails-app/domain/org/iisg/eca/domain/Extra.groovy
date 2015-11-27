package org.iisg.eca.domain

/**
 * Domain class of table holding all extras for a particular event
 */
class Extra extends EventDateDomain {
    String title
    String extra
    String description
    String secondDescription
    BigDecimal amount = BigDecimal.ZERO
	boolean isFinalRegistration = true
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
    }

	static hibernateFilters = {
		dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
		hideDeleted(condition: 'deleted = 0', default: true)
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
		    'sortOrder'
    ]

	void softDelete() {
		deleted = true
	}

    @Override
    String toString() {
        extra
    }
}
