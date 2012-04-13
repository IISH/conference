package org.iisg.eca

/**
 * Domain class of table holding all existing networks
 */
class Network extends DefaultDomain {
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

    @Override
    String toString() {
        name
    }
}
