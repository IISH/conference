package org.iisg.eca.domain

/**
 * Domain class of table holding all titles
 */
class Title extends DefaultDomain {
   // def pageInformation

    Event event
    String title

    static belongsTo = Event

    static constraints = {
        event   nullable: true
        title   blank: false,   maxSize: 30
    }

    static mapping = {
        table 'titles'
        version false

        id      column: 'title_id'
        event   column: 'event_id'
        title   column: 'title'
    }

    static hibernateFilters = {
        eventFilter(condition: "event_id = :eventId  or event_id = null", types: 'long')
    }

   /* def beforeLoad() {
        if (pageInformation.date) {
            enableHibernateFilter('eventFilter').setParameter('eventId', pageInformation.date.event.id)
        }
    }    */

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
        title
    }
}
