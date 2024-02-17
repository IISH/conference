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

        participantVolunteering cascade: 'all-delete-orphan'
    }

    static constraints = {
        description blank: false,   maxSize: 30
    }

	static apiActions = ['GET']

	static apiAllowed = [
			'id',
			'description'
	]

    @Override
    String toString() {
        description
    }
}
