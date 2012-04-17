package org.iisg.eca

/**
 * Domain class of table holding all sessions
 */
class Session extends DefaultDomain {
    EventDate date
    String code
    String name
    String comment
    String abstr

    static belongsTo = Date

    static mapping = {
		table 'sessions'
 		version false

        id          column: 'session_id'
        date        column: 'date_id'
        code        column: 'session_code'
        name        column: 'session_name'
        comment     column: 'session_comment',  type: 'text'
        abstr       column: 'session_abstract', type: 'text'
	}

	static constraints = {
		date        nullable: true
        code        blank: false,   maxSize: 10
        name        blank: false,   maxSize: 255
        comment     blank: false
        abstr       blank: false
	}

    @Override
    String toString() {
        name
    }
}
