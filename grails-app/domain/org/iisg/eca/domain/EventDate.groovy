package org.iisg.eca.domain

import org.springframework.context.i18n.LocaleContextHolder

/**
 * Domain class of table holding all event dates
 */
class EventDate  {
	def messageSource

	String yearCode
    Date startDate
    Date endDate
    String dateAsText
    String description
    String longDescription
    boolean createStatistics = true
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
		cache true
        version false

        id                  column: 'date_id'
        yearCode            column: 'year_code'
        startDate           column: 'start_date',       sqlType: 'date'
        endDate             column: 'end_date',         sqlType: 'date'
        dateAsText          column: 'date_as_text'
        description         column: 'description'
        longDescription     column: 'long_description', sqlType: 'text'
        createStatistics    column: 'create_statistics'
        event               column: 'event_id',			fetch: 'join'

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
			'lastDate',
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

	    getDateAndLaterDates { date ->
		    eq('event.id', date.event.id)
		    ge('startDate', date.startDate)
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

	/**
	 * Returns a version of this event date for usage in an URL
	 * @return A version of this event date for usage in an URL
	 */
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

	/**
	 * Returns '{yearCode} and later...' unless it is the latest year
	 * @return '{yearCode} and later...' or '{yearCode}'
	 */
	String getDateAndLaterText() {
		if (this.isLastDate()) {
			this.yearCode
		}
		else {
			messageSource.getMessage('eventDate.andLater.label', [this.yearCode] as String[], LocaleContextHolder.locale)
		}
	}

	/**
	 * Returns the short name of the event + the year code
	 * @return The short name of the event + the year code
	 */
	String getShortNameAndYear() {
		return "${this.event.shortName} ${this.yearCode}";
	}

    @Override
    String toString() {
        "${description} ${dateAsText}"
    }
}
