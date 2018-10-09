package org.iisg.eca.domain

/**
 * Domain class of table holding all existing networks
 */
class Network extends EventDateDomain {
    def pageInformation

    String name
    String comment
    String longDescription
    String url
    String email
    boolean showOnline = true
	boolean deleted = false
    
    static hasMany = [  chairs: NetworkChair,
                        participantVolunteering: ParticipantVolunteering,
                        paperProposals: Paper,
                        sessions: Session]

    static constraints = {
        name            blank: false,   maxSize: 100
        comment         nullable: true
        longDescription nullable: true
        url             nullable: true, maxSize: 255
        email           nullable: true, maxSize: 100,   email: true
    }

    static mapping = {
        table 'networks'
        version false
        sort name: 'asc'

        id              column: 'network_id'
        name            column: 'name'
        comment         column: 'comment',          type: 'text'
        longDescription column: 'long_description', type: 'text'
        url             column: 'url'
        email           column: 'email'
        showOnline      column: 'show_online'
	    deleted         column: 'deleted'

	    chairs                      sort: 'isMainChair', order: 'desc', cascade: 'all-delete-orphan'
        sessions                    joinTable: 'session_in_network'
        participantVolunteering     cascade: 'all-delete-orphan'
    }

	static hibernateFilters = {
		dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
		hideDeleted(condition: 'deleted = 0', default: true)
	}

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'name',
            'comment',
            'longDescription',
            'url',
            'email',
            'showOnline',
            'chairs.chair.id',
    ]

	void softDelete() {
		deleted = true
	}
    
    @Override
    String toString() {
        name
    }
}
