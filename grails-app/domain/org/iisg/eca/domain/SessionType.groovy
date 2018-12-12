package org.iisg.eca.domain

/**
 * Domain class of table holding all possible session types
 */
class SessionType extends EventDateDomain {
    String type

    static hasMany = [sessions: Session]

    static mapping = {
        table 'session_types'
        cache true
        version false

        id      column: 'session_type_id'
        type    column: 'type'
    }
    
    static constraints = {
        type    blank: false,   maxSize: 100
    }

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'type'
    ]

    @Override
    String toString() {
        type
    }
}
