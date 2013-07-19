package org.iisg.eca.domain

/**
 * Domain class of table holding all possible session states
 */
class SessionState extends EventDateDomain {
    String description
    String shortDescription
    PaperState correspondingPaperState

    static hasMany = [sessions: Session]

    static mapping = {
        table 'session_states'
        version false

        id                      column: 'session_state_id'
        description             column: 'description'
        shortDescription        column: 'short_description'
        correspondingPaperState column: 'corresponding_paper_state_id'
    }
    
    static constraints = {
        description             blank: false,   maxSize: 50
        shortDescription        blank: false,   maxSize: 10
        correspondingPaperState nullable: true
    }

    @Override
    String toString() {
        description
    }
}
