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

    static belongsTo = ParticipantDate
    static hasMany = [participantDates: ParticipantDate]

    static mapping = {
        table 'extras'
        version false

        id                  column: 'extra_id'
        title               column: 'title'
        extra               column: 'extra'
        description         column: 'description'
        secondDescription   column: 'description_2nd',  type: 'text'
        amount              column: 'amount'

        participantDates    joinTable: 'participant_date_extra'
    }

    static constraints = {
        title               nullable: true, masSize: 30
        extra               blank: false,   maxSize: 30
        description         nullable: true, maxSize: 100
        secondDescription   nullable: true
        amount              min: BigDecimal.ZERO
    }

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'title',
            'extra',
            'description',
            'secondDescription',
            'amount'
    ]

    @Override
    String toString() {
        extra
    }
}
