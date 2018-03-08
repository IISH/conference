package org.iisg.eca.domain

/**
 * Domain class of table holding all papers, ONLY for listing papers (it does not include all info)
 */
class PaperList extends EventDateDomain {
    User user
    PaperState state
    Session session
    String title
    PaperType type
    Network networkProposal
    String sessionProposal
    BigDecimal avgReviewScore
	boolean deleted = false

	static belongsTo = [User, PaperState, Session, PaperType, Network]
    static hasMany = [reviews: PaperReview]

    static mapping = {
        table 'papers'
        version false

        id                  column: 'paper_id'
	    user                column: 'user_id',              fetch: 'join'
        state               column: 'paper_state_id',       fetch: 'join'
        session             column: 'session_id',           fetch: 'join'
        title               column: 'title'
        type                column: 'paper_type_id'
        networkProposal     column: 'network_proposal_id',  fetch: 'join'
        sessionProposal     column: 'session_proposal'
        avgReviewScore		column: 'avg_review_score'
	    deleted             column: 'deleted'

        reviews             joinTable: [name: 'paper_reviews', key: 'paper_id'],    fetch: 'join'
    }

    static constraints = {
        session             nullable: true
        title               blank: false,   maxSize: 500
        type            	nullable: true
        networkProposal     nullable: true
        sessionProposal     nullable: true, maxSize: 500
        avgReviewScore		nullable: true
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
