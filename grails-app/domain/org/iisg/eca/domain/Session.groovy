package org.iisg.eca.domain

import groovy.sql.Sql

/**
 * Domain class of table holding all sessions
 */
class Session extends EventDateDomain {
    def dataSource

    String code
    String name
    String abstr
    String comment
    SessionState state
    boolean mailSessionState = true
    User addedBy
	boolean deleted = false

    static belongsTo = [Network, SessionState]
    static hasMany = [  sessionParticipants: SessionParticipant,
                        papers: Paper,
                        sessionRoomDateTime: SessionRoomDateTime,
                        networks: Network]

    static mapping = {
        table 'sessions'
        version false
        sort code: 'asc'

        id                  column: 'session_id'
        date                column: 'date_id'
        code                column: 'session_code'
        name                column: 'session_name'
        abstr               column: 'session_abstract', type: 'text'
        comment             column: 'session_comment',  type: 'text'
        state               column: 'session_state_id'
        mailSessionState    column: 'mail_session_state'
        addedBy             column: 'added_by'
	    deleted             column: 'deleted'

        networks                joinTable: 'session_in_network'
        sessionParticipants     cascade: 'all-delete-orphan'
        sessionRoomDateTime     cascade: 'all-delete-orphan'  
    }

    static constraints = {
        date        nullable: true
        code        nullable: true, maxSize: 10
        name        blank: false,   maxSize: 255
        abstr       nullable: true
        comment     nullable: true
        addedBy     nullable: true
    }

	static hibernateFilters = {
		dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
		hideDeleted(condition: 'deleted = 0', default: true)
	}

    static apiActions = ['GET', 'POST', 'PUT', 'DELETE']

    static apiAllowed = [
            'id',
            'name',
            'abstr',
            'state.id',
            'papers.id',
            'networks.id',
            'addedBy.id'
    ]

	static apiPostPut = [
			'name',
			'abstr',
			'state.id',
			'networks.id',
			'addedBy.id'
	]

	void softDelete() {
		deleted = true
	}

	void updateForApi(String property, String value) {
		switch (property) {
			case 'addedBy.id':
				User addedBy = (value.isLong()) ? User.get(value.toLong()) : null
				if (addedBy) {
					this.addedBy = addedBy
				}
				break
			case 'state.id':
				SessionState state = (value.isLong()) ? SessionState.get(value.toLong()) : null
				if (state) {
					this.state = state
				}
				break
			case 'networks.id':
				List<Network> networks = []
				networks += this.networks
				networks.each { it?.removeFromSessions(this) }
				this.save(flush: true)
				value.split(';').each { networkId ->
					if (networkId.toString().isLong()) {
						Network network = Network.findById(networkId.toString().toLong())
						if (network) {
							this.addToNetworks(network)
						}
					}
				}
				break
		}
	}

    /**
     * Updates the paper state of all papers of this session when the state of this session has been changed
     */
    def beforeUpdate() {
        if (isDirty('state')) { 
            mailSessionState = true
            updatePaperStates()
        }
    }
    
    void updatePaperStates() {
        if (state.correspondingPaperState) {
            Sql sql = new Sql(dataSource)
            PaperState correspondingPaperState = state.correspondingPaperState

            // Change all paper states of this session
            Paper.findAllBySession(this).each { paper ->
                // Only update papers that may be changed
                if (paper.state.sessionStateTrigger) {
                    // Use SQL to prevent Hibernate session exceptions
                    sql.executeUpdate('''
                      UPDATE papers
                      SET paper_state_id = :paperStateId
                      WHERE paper_id = :paperId
                    ''', [paperStateId: correspondingPaperState.id, paperId: paper.id])
                }
            }
        }
    }

    /**
     * Find out the date/time this session is planned
     * @return The date/time the session is planned, if it is planned, otherwise <code>null</code> is returned.
     */
    SessionDateTime getPlannedDateTime() {
        SessionRoomDateTime.findBySession(this)?.sessionDateTime
    }

    /**
     * Returns all participant types that are scheduled in this session
     * @return All participant types that are scheduled in this session
     */
    List<ParticipantType> getAllParticipantTypes() {
        ParticipantType.executeQuery('''
            SELECT DISTINCT t
            FROM ParticipantType AS t
            INNER JOIN t.sessionParticipants AS sp
            WHERE sp.session.id = :sessionId
        ''', [sessionId: this.id])
    }
    
    @Override
    String toString() {
        String readCode = (code) ? code : "-";
        "${readCode}: ${name}"
    }
}
