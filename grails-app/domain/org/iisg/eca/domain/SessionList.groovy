package org.iisg.eca.domain

import org.iisg.eca.filter.SoftDelete

/**
 * Domain class of table holding all sessions, ONLY for listing sessions (it does not include all info)
 */
@SoftDelete
class SessionList extends EventDateDomain {
    def dataSource

    String code
    String name
    SessionState state
    SessionType type
	boolean deleted = false

    static belongsTo = SessionState

    static mapping = {
        table 'sessions'
        version false

        id      column: 'session_id'
        code    column: 'session_code'
        name    column: 'session_name'
        state   column: 'session_state_id', fetch: 'join'
        type    column: 'session_type_id',  fetch: 'join'
	    deleted column: 'deleted'
    }

    static constraints = {
        date        nullable: true
        code        nullable: true, maxSize: 10
        name        blank: false,   maxSize: 255
        type        nullable: true
    }

    @Override
    String toString() {
        String readCode = (code) ? code : "-";        
        "${readCode}: ${name}"
    }
}
