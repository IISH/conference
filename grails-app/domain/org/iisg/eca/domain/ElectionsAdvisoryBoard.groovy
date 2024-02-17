package org.iisg.eca.domain

/**
 * Domain class of table holding all candidates for the election for the advisory board
 */
class ElectionsAdvisoryBoard extends EventDateDomain {
	String nameCandidate
	int noOfVotes = 0

	static constraints = {
		nameCandidate   blank: false, maxSize: 100
	}

	static mapping = {
		table 'elections_advisory_board'
		version false
		sort nameCandidate: 'asc'

		id              column: 'elections_advisory_board_id'
		nameCandidate   column: 'name_candidate'
		noOfVotes       column: 'no_of_votes'
	}

	static apiActions = ['GET', 'POST']

	static apiAllowed = [
			'id',
			'nameCandidate',
			'noOfVotes'
	]

	static apiPostPut = [
			'noOfVotes'
	]

	String toString() {
		return "$nameCandidate ($noOfVotes)"
	}
}
