package org.iisg.eca.domain

/**
 * Domain class of table holding all reviewers
 */
class Reviewer extends EventDateDomain {
    User user
    Boolean confirmed

    static belongsTo = [User]

    static mapping = {
        table 'reviewers'
        version false

        id          column: 'reviewer_id'
        user        column: 'user_id',      fetch: 'join'
        confirmed   column: 'confirmed'
    }

    static apiActions = ['GET', 'POST']

    static apiAllowed = [
            'id',
		    'user.id',
		    'confirmed'
    ]

	static apiPostPut = [
			'confirmed'
	]

    static hibernateFilters = {
        dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
    }

    @Override
    String toString() {
        return user.toString()
    }
}
