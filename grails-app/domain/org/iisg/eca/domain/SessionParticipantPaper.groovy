package org.iisg.eca.domain

/**
 * Domain class of a view linking a session participant with a paper
 */
class SessionParticipantPaper implements Serializable {
    SessionParticipant sessionParticipant
    Paper paper

    static mapping = {
        table 'vw_session_participant_papers'
        version false

		id 					composite: ['sessionParticipant', 'paper']
		sessionParticipant 	column: 'session_participant_id'
        paper    			column: 'paper_id'
	}
}
