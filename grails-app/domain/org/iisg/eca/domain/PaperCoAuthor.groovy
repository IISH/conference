package org.iisg.eca.domain

/**
 * Domain class of table holding all co-authors of papers
 */
class PaperCoAuthor extends EventDateDomain {
    User user
    Paper paper
	User addedBy

    static belongsTo = [User, Paper]

    static mapping = {
        table 'paper_coauthors'
        version false

        id      column: 'paper_coauthor_id'
        user    column: 'user_id',          fetch: 'join'
        paper   column: 'paper_id',         fetch: 'join'
		addedBy column: 'added_by',         fetch: 'join'
	}

	static constraints = {
		addedBy nullable: true
	}

    static apiActions = ['GET', 'POST', 'PUT', 'DELETE']

    static apiAllowed = [
            'id',
            'user.id',
            'paper.id',
			'addedBy.id',
			'user',
			'paper',
			'addedBy'
	]

	static apiPostPut = [
			'user.id',
			'paper.id',
			'addedBy.id'
	]

	void updateForApi(String property, String value) {
		switch (property) {
			case 'user.id':
				User user = User.get(value.toLong())
				if (user) {
					this.user = user
				}
				break
			case 'paper.id':
				Paper paper = Paper.findById(value.toLong())
				if (paper) {
					this.paper = paper
				}
				break
			case 'addedBy.id':
				User addedBy = User.get(value.toLong())
				if (addedBy) {
					this.addedBy = addedBy
				}
				break
		}
	}

    @Override
    String toString() {
        "${user} (${paper})"
    }
}
