package org.iisg.eca.domain

/**
 * Domain class of table holding all papers, ONLY for listing papers (it does not include all info)
 */
class PaperList extends EventDateDomain {
    User user
    PaperState state
    Session session
    String title
    Network networkProposal
    String sessionProposal
	boolean deleted = false

	static belongsTo = [User, PaperState, Session, Network]

    static mapping = {
        table 'papers'
        version false

        id                  column: 'paper_id'
	    user                column: 'user_id',              fetch: 'join'
        state               column: 'paper_state_id',       fetch: 'join'
        session             column: 'session_id',           fetch: 'join'
        title               column: 'title'
        networkProposal     column: 'network_proposal_id',  fetch: 'join'
        sessionProposal     column: 'session_proposal'
	    deleted             column: 'deleted'
    }

    static constraints = {
        session             nullable: true
        title               blank: false,   maxSize: 500
        networkProposal     nullable: true
        sessionProposal     nullable: true, maxSize: 500
    }

	static hibernateFilters = {
		dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
		hideDeleted(condition: 'deleted = 0', default: true)
	}

    @Override
    String toString() {
        title
    }
}
