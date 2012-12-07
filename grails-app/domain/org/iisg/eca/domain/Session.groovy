package org.iisg.eca.domain

/**
 * Domain class of table holding all sessions
 */
class Session extends EventDateDomain {
    String code
    String name
    String abstr
    String comment

    static belongsTo = Network
    static hasMany = [  sessionParticipants: SessionParticipant,
                        papers: Paper,
                        sessionRoomDateTime: SessionRoomDateTime,
                        networks: Network]

    static mapping = {
        table 'sessions'
        version false
        sort code: 'asc'

        id          column: 'session_id'
        date        column: 'date_id'
        code        column: 'session_code'
        name        column: 'session_name'
        abstr       column: 'session_abstract', type: 'text'
        comment     column: 'session_comment',  type: 'text'

        networks                joinTable: 'session_in_network'
        sessionParticipants     cascade: 'all-delete-orphan'
        sessionRoomDateTime     cascade: 'all-delete-orphan'  
    }

    static constraints = {
        date        nullable: true
        code        blank: false,   maxSize: 10
        name        blank: false,   maxSize: 255
        abstr       nullable: true
        comment     nullable: true
    }
    
    @Override
    String toString() {
        "${code}: ${name}"
    }
}
