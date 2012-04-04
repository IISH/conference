package org.iisg.eca

class Network extends DefaultDomain {
    EventDate date
    String name
    String comment
    boolean showOnline = true
    boolean showInternal = true

    static belongsTo = EventDate

    static constraints = {
        date    nullable: true
        name    blank: false,   maxSize: 30
        comment nullable: true
    }

    static mapping = {
        table 'networks'
        version false

        id      column: 'network_id'
        date    column: 'date_id'
        name    column: 'name'
        comment column: 'comment',      type: 'text'
    }

    @Override
    String toString() {
        name
    }
}
