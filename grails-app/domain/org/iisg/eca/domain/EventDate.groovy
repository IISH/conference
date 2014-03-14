package org.iisg.eca.domain

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
    boolean createStatistics = false
    Event event

    static hasMany = [days: Day]
    static belongsTo = Event

    static constraints = {
        startDate       nullable: true
        endDate         nullable: true
        yearCode        blank: false,   maxSize: 20
        dateAsText      blank: false,   maxSize: 30
        description     blank: false,   maxSize: 255
        longDescription nullable: true
    }

    static mapping = {
        table 'dates'
        version false

        id                  column: 'date_id'
        yearCode            column: 'year_code'
        startDate           column: 'start_date',       sqlType: 'date'
        endDate             column: 'end_date',         sqlType: 'date'
        dateAsText          column: 'date_as_text'
        description         column: 'description'
        longDescription     column: 'long_description', sqlType: 'text'
        createStatistics    column: 'create_statistics'
        event               column: 'event_id'

        days                sort: 'dayNumber',  order: 'asc'
        sort                startDate: 'desc'
    }

	static apiActions = ['GET']

	static apiAllowed = [
			'id',
			'yearCode',
			'startDate',
			'endDate',
			'dateAsText',
			'description',
			'longDescription',
			'event'
	]

    static namedQueries = {
        sortByEventAndDate {
            event {
                order('shortName', 'asc')
            }
            order('startDate', 'desc')
            cache(true)
        }

        getAllForEvent { event ->
            eq('event.id', event.id)
            order('startDate', 'desc')
            cache(true)
        }
    }

	/**
	 * Returns the last event date of the given event
	 * @param event The event in question
	 * @return The last event date of the given event
	 */
	static EventDate getLastDate(Event event) {
		return executeQuery('''
			SELECT d
			FROM Event AS e
			INNER JOIN e.dates AS d
			WHERE e.id = :eventId
			ORDER BY d.startDate DESC
		''', ['eventId': event.id], [cache: true]).first()
	}

    String getUrl() {
        yearCode.replaceAll('\\s', '-')
    }

	/**
	 * Whether of all dates belonging to the event this is also the last date
	 * @return Whether this is or is not currently the last date of the event
	 */
	boolean isLastDate() {
		return getLastDate(this.event).equals(this)
	}

    @Override
    String toString() {
        "${description} ${dateAsText}"
    }
}
