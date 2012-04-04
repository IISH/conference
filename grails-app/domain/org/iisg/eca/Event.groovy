package org.iisg.eca

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

        dates       sort: 'startDate',  order: 'desc'
	}

	static constraints = {
        code        maxSize: 20,    blank: false
		shortName   maxSize: 20,    blank: false
		longName    maxSize: 50,    blank: false
		type        maxSize: 20,    nullable: true
	}

    @Override
    def String toString() {
        shortName
    }
}
