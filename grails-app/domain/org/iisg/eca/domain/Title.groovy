package org.iisg.eca.domain

/**
 * Domain class of table holding all titles
 */
class Title extends EventDomain {
    String title

    static constraints = {
        event   nullable: true
        title   blank: false,   maxSize: 20
    }

    static mapping = {
        table 'titles'
        version false

        id      column: 'title_id'
        event   column: 'event_id'
        title   column: 'title'
    }
    
    @Override
    String toString() {
        title
    }
}
