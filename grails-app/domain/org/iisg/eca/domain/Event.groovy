package org.iisg.eca.domain

/**
 * Domain class of table holding all events
 */
class Event extends DefaultDomain {
    String code
    String shortName
    String longName
    String type

    static hasMany = [dates: EventDate]

    static mapping = {
        table 'events'
        version false

        id          column: 'event_id'
        code        column: 'code'
        shortName   column: 'short_name'
        longName    column: 'long_name'
        type        column: 'type'

        sort        'shortName'                
        dates       sort: 'startDate',  order: 'desc'
    }

    static constraints = {
        code        maxSize: 20,    blank: false,   unique: true
        shortName   maxSize: 20,    blank: false
        longName    maxSize: 50,    blank: false,   widget: 'textarea'
        type        maxSize: 20,    nullable: true
    }

	static apiActions = ['GET']

	static apiAllowed = [
			'id',
			'code',
			'shortName',
			'longName'
	]

		// Loop over all the events and collect the event dates
		Map<Event, List<EventDate>> datesByEvent = [:]
		events.each { event ->
			// Perhaps the user only has access to the last even date?
			List<Role> roles = Role.findAllByOnlyLastDate(true, [cache: true])
			if (user.getRoles().find { roles.contains(it) }) {
				datesByEvent.put(event, [EventDate.getLastDate(event)])
			}
			else {
				datesByEvent.put(event, EventDate.getAllForEvent(event).list())
			}
		}

		return datesByEvent
	}

    String getUrl() {
        code.replaceAll('\\s', '-')
    }

    def beforeInsert() {
        // Make sure the code is in lowercase
        code = code.toLowerCase()
    }

    def beforeUpdate() {
        // Make sure the code is in lowercase
        code = code.toLowerCase()
    }

    @Override
    String toString() {
        shortName
    }
}
