package org.iisg.eca.domain

/**
 * Domain class of table holding all existing networks
 */
class Network extends DefaultDomain {
    def pageInformation

    EventDate date
    String name
    String comment
    String url
    boolean showOnline = true
    boolean showInternal = true

    static belongsTo = EventDate
    static hasMany = [chairs: NetworkChair]

    static constraints = {
        date    nullable: true
        name    blank: false,   maxSize: 30
        comment nullable: true
        url     blank: false,   maxSize: 255
    }

    static mapping = {
        table 'networks'
        version false

        id      column: 'network_id'
        date    column: 'date_id'
        name    column: 'name'
        comment column: 'comment',      type: 'text'
        url     column: 'url'
    }

    static hibernateFilters = {
        eventDateFilter(condition: "date_id = (:dateId) or date_id = null", types: 'long')
    }

   /* def beforeLoad() {
        if (pageInformation.date) {
            enableHibernateFilter('eventDateFilter').setParameter('dateId', pageInformation.date.id)
        }
    }   */

    /*def beforeInsert() {
        if (pageInformation.date) {
            date = pageInformation.date
        }
    }

    def beforeUpdate() {
        if (pageInformation.date) {
            date = pageInformation.date
        }
    }     */

    @Override
    String toString() {
        name
    }
}
