package org.iisg.eca.domain

/**
 * Domain class of table holding all volunteering possibilities for participants
 */
class Volunteering extends EventDomain {
    String description

    static hasMany = [participantVolunteering: ParticipantVolunteering]

    static mapping = {
        table 'volunteering'
        version false

        id          column: 'volunteering_id'
        description column: 'description'
    }

    static constraints = {
        description blank: false,   maxSize: 30
    }

    @Override
    String toString() {
        description
    }
}
