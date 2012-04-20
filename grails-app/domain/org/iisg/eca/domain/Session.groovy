package org.iisg.eca.domain

/**
 * Domain class of table holding all sessions
 */
class Session extends DefaultDomain {
   // def pageInformation

    EventDate date
    String code
    String name
    String comment

    static belongsTo = Date

    static mapping = {
		table 'sessions'
 		version false

        id          column: 'session_id'
        date        column: 'date_id'
        code        column: 'session_code'
        name        column: 'session_name'
        comment     column: 'session_comment',  type: 'text'
	}

	static constraints = {
		date        nullable: true
        code        blank: false,   maxSize: 10
        name        blank: false,   maxSize: 255
        comment     nullable: true
	}

    static hibernateFilters = {
        eventDateFilter(condition: "date_id = (:dateId) or date_id = null", types: 'long')
    }

   /* def beforeLoad() {
        if (pageInformation.date) {
            enableHibernateFilter('eventDateFilter').setParameter('dateId', pageInformation.date.id)
        }
    } */

   /* def beforeInsert() {
        if (pageInformation.date) {
            date = pageInformation.date
        }
    }

    def beforeUpdate() {
        if (pageInformation.date) {
            date = pageInformation.date
        }
    }    */

    @Override
    String toString() {
        name
    }
}
