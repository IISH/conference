package org.iisg.eca.domain

class Extra extends EventDateDomain {
    String extra

    static belongsTo = ParticipantDate
    static hasMany = [participantDates: ParticipantDate]

    static mapping = {
        table 'extras'
        version false

        id      column: 'extra_id'
        extra   column: 'extra'

        participantDates    joinTable: 'participant_date_extra'
    }

    static constraints = {
        extra   blank: false,   maxSize: 30
    }

    @Override
    String toString() {
        extra
    }
}
