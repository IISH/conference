package org.iisg.eca.domain

/**
 * Domain class of table holding all possible session states
 */
class SessionState extends EventDateDomain {
    String description

    static hasMany = [sessions: Session]

    static mapping = {
        table 'session_states'
        version false

        id          column: 'session_state_id'
        description column: 'description'
    }
    
    static constraints = {
        description blank: false,   maxSize: 50
    }

    @Override
    String toString() {
        description
    }
}
