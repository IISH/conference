package org.iisg.eca.domain

/**
 * Domain class of table holding all participants of a session
 */
class SessionParticipant extends EventDateDomain {
    User user
    Session session
    ParticipantType type
	User addedBy

    static belongsTo = [User, Session, ParticipantType]
	static hasMany = [sessionParticipantPapers: SessionParticipantPaper]

    static mapping = {
        table 'session_participant'
        version false
        type sort: 'importance', order: 'desc'

        id      column: 'session_participant_id'
        user    column: 'user_id',              fetch: 'join'
        session column: 'session_id',           fetch: 'join'
        type    column: 'participant_type_id',  fetch: 'join'
	    addedBy column: 'added_by',             fetch: 'join'

		sessionParticipantPapers cascade: 'none'
	}

	static constraints = {
		addedBy nullable: true
	}

    static apiActions = ['GET', 'POST', 'PUT', 'DELETE']

    static apiAllowed = [
            'id',
            'user.id',
            'session.id',
            'type.id',
		    'addedBy.id',
            'user',
            'session',
            'type',
		    'addedBy'
    ]

	static apiPostPut = [
			'user.id',
			'session.id',
			'type.id',
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
			case 'session.id':
				Session session = Session.findById(value.toLong())
				if (session) {
					this.session = session
				}
				break
			case 'type.id':
				ParticipantType type = ParticipantType.findById(value.toLong())
				if (type) {
					this.type = type
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
        "${user} (${type})"
    }
}
