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

<<<<<<< HEAD
	static apiActions = ['GET']

	static apiAllowed = [
			'id',
			'code',
			'shortName',
			'longName'
	]
=======
	/**
	 *  Returns a map with all the events and its event dates the given user has access to
	 * @param user The user in question
	 * @return A map with all the events and its event dates the given user has access to
	 */
	static Map<Event, List<EventDate>> getEventsAndDatesWithAccess(User user) {
		List<Event> events = user.events

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
>>>>>>> 802dfd05ae0ec9d22ca1f222bacfde7af07a5044

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
