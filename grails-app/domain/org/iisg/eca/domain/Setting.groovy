package org.iisg.eca.domain

/**
 * Domain class of table holding all settings
 */
class Setting {
    //def pageInformation

	String property
	String value
    Event event

    static belongsTo = Event

	static mapping = {
        table 'settings'
		version false
        cache true

        id          column: 'setting_id'
        property    column: 'property'
        value       column: 'value'
        event       column: 'event_id'
	}

	static constraints = {
		property    blank: false,   maxSize: 50
        value       blank: false,   maxSize: 255
		event       nullable: true
	}

    static hibernateFilters = {
        eventFilter(condition: "event_id = :eventId or event_id = null", types: 'long')
    }

    /*def beforeLoad() {
        if (pageInformation.date) {
            enableHibernateFilter('eventFilter').setParameter('eventId', pageInformation.date.event.id)
        }
    }     */

   /* def beforeInsert() {
        if (pageInformation.date) {
            event = pageInformation.date.event
        }
    }

    def beforeUpdate() {
        if (pageInformation.date) {
            event = pageInformation.date.event
        }
    }  */

    @Override
    String toString() {
        "${property}: ${value}"
    }
}
