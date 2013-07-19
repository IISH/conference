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
        event               column: 'event_id'

        days                sort: 'dayNumber',  order: 'asc'
        sort                startDate: 'desc'
    }

    static namedQueries = {
        sortByEventAndDate {
            event {
                order('shortName', 'asc')
            }
            order('startDate', 'desc')
        }
    }

    String getUrl() {
        yearCode.replaceAll('\\s', '-')
    }

    @Override
    String toString() {
        "${description} ${dateAsText}"
    }
}
