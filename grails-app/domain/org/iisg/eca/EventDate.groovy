package org.iisg.eca

/**
 * Domain class of table holding all event dates
 */
class EventDate extends DefaultDomain {
	String yearCode
	Date startDate
	Date endDate
	String dateAsText
	String description
	String longDescription
    Event event

    static hasMany = [days: Day]
	static belongsTo = Event

    static constraints = {
        startDate       nullable: true
        endDate         nullable: true
		yearCode        maxSize: 20,    blank: false
		dateAsText      maxSize: 30,    blank: false
        description     maxSize: 255,   blank: false
		longDescription maxSize: 65535, nullable: true
        days            nullable: true
	}

	static mapping = {
        table 'dates'
 		version false

        id              column: 'date_id'
        yearCode        column: 'year_code'
        startDate       column: 'start_date',        sqlType: 'date'
        endDate         column: 'end_date',          sqlType: 'date'
        dateAsText      column: 'date_as_text'
        description     column: 'description'
        longDescription column: 'long_description',  sqlType: 'text'
        event           column: 'event_id'

        days            sort: 'dayNumber'
	}

    static namedQueries = {
        sortByEventAndDate {
            event {
                order('shortName', 'asc')
            }
            order('startDate', 'desc')
        }
    }

    @Override
    def String toString() {
        yearCode
    }
}
