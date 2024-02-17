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

    /**
     * Returns all the users that have been accepted as participants in accepted sessions that fall in this network
     * @return A list of users
     */
    List<User> getAllUsersInNetwork() {
        return User.executeQuery('''
            SELECT DISTINCT u
            FROM User AS u
            INNER JOIN u.participantDates as pd
            INNER JOIN u.combinedSessionParticipants AS sp
            INNER JOIN sp.session AS s
            INNER JOIN s.networks AS n
            WHERE pd.date.id = :dateId
            AND s.date.id = :dateId
            AND n.date.id = :dateId
            AND pd.deleted = false
            AND s.deleted = false
            AND n.deleted = false
            AND n.id = :networkId
            AND s.state.id IN (:sessionNew, :sessionAccepted, :sessionInConsideration)
            AND pd.state.id IN (:newParticipant, :dataChecked, :participant, :notFinished, :onlineParticipant)
            ORDER BY u.lastName, u.firstName, u.email
        ''', [  dateId                 : pageInformation.date.id,
                networkId              : this.id,
                sessionNew             : SessionState.NEW_SESSION,
                sessionAccepted        : SessionState.SESSION_ACCEPTED,
                sessionInConsideration : SessionState.SESSION_IN_CONSIDERATION,
                newParticipant         : ParticipantState.NEW_PARTICIPANT,
                dataChecked            : ParticipantState.PARTICIPANT_DATA_CHECKED,
                participant            : ParticipantState.PARTICIPANT,
                notFinished            : ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION,
                onlineParticipant      : ParticipantState.ONLINE_PARTICIPANT
        ])
    }

    @Override
    String toString() {
        name
    }
}
