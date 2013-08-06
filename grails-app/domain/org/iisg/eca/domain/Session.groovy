package org.iisg.eca.domain

/**
 * Domain class of table holding all sessions
 */
class Session extends EventDateDomain {
    String code
    String name
    String abstr
    String comment
    SessionState state
    boolean mailSessionState = true
    
    static belongsTo = [Network, SessionState]
    static hasMany = [  sessionParticipants: SessionParticipant,
                        papers: Paper,
                        sessionRoomDateTime: SessionRoomDateTime,
                        networks: Network]

    static mapping = {
        table 'sessions'
        version false
        sort code: 'asc'

        id                  column: 'session_id'
        date                column: 'date_id'
        code                column: 'session_code'
        name                column: 'session_name'
        abstr               column: 'session_abstract', type: 'text'
        comment             column: 'session_comment',  type: 'text'
        state               column: 'session_state_id'
        mailSessionState    column: 'mail_session_state'

        networks                joinTable: 'session_in_network'
        sessionParticipants     cascade: 'all-delete-orphan'
        sessionRoomDateTime     cascade: 'all-delete-orphan'  
    }

    static constraints = {
        date        nullable: true
        code        nullable: true, maxSize: 10
        name        blank: false,   maxSize: 255
        abstr       nullable: true
        comment     nullable: true
    }

    /**
     * Updates the paper state of all papers of this session when the state of this session has been changed
     */
    def beforeUpdate() {
        if (isDirty('state')) { 
            mailSessionState = true
            updatePaperStates()
        }
    }
    
    void updatePaperStates() {
        if (state.correspondingPaperState) {
            PaperState correspondingPaperState = state.correspondingPaperState

            // Change all paper states of this session
            Paper.findAllBySession(this).each { paper ->
                // Only update papers that may be changed
                if (paper.state.sessionStateTrigger) {
                    paper.state = correspondingPaperState
                }
            }
        }
    }
    
    @Override
    String toString() {
        String readCode = (code) ? code : "-";        
        "${readCode}: ${name}"
    }
}
