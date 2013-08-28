package org.iisg.eca.domain

import groovy.sql.Sql

/**
 * Domain class of table holding all sessions, ONLY for listing sessions (it does not include all info)
 */
class SessionList extends EventDateDomain {
    def dataSource

    String code
    String name
    SessionState state
    
    static belongsTo = SessionState

    static mapping = {
        table 'sessions'
        version false

        id                  column: 'session_id'
        date                column: 'date_id'
        code                column: 'session_code'
        name                column: 'session_name'
        state               column: 'session_state_id'
    }

    static constraints = {
        date        nullable: true
        code        nullable: true, maxSize: 10
        name        blank: false,   maxSize: 255
    }

    @Override
    String toString() {
        String readCode = (code) ? code : "-";        
        "${readCode}: ${name}"
    }
}
