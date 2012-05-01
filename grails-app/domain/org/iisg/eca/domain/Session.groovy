package org.iisg.eca.domain

/**
 * Domain class of table holding all sessions
 */
class Session extends EventDateDomain {
    String code
    String name
    String comment

    static hasMany = [sessionParticipants: SessionParticipant]

    static mapping = {
        table 'sessions'
        version false

        id          column: 'session_id'
        date        column: 'date_id'
        code        column: 'session_code'
        name        column: 'session_name'
        comment     column: 'session_comment',  type: 'text'
    }

    static constraints = {
        date        nullable: true
        code        blank: false,   maxSize: 10
        name        blank: false,   maxSize: 255
        comment     nullable: true
    }
    
    @Override
    String toString() {
        name
    }
}
