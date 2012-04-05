package org.iisg.eca

class Title extends DefaultDomain {
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
}
