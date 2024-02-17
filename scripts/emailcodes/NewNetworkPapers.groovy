def noSessionInfo = true
def sessionsText = ""

def noPaperInfo = true
def papersText = ""

def shortMailSeparator = "\n\n- - - -\n\n";
def longMailSeparator = "\n\n+ + + + + + + + + + +\n\n";

// SESSION PAPERS

def sessions = sql.rows("""
	SELECT DISTINCT	s.session_id, s.session_name, s.session_abstract
	FROM			sessions s
	INNER JOIN 		session_in_network AS sin
	ON 				s.session_id = sin.session_id
	WHERE 			s.date_id = :dateId
	AND				s.deleted = 0
	AND				sin.network_id = :networkId
	ORDER BY		s.session_name ASC
""", params)

if (sessions?.size() > 0) {
    def sessionsInfo = sessions.collect { session ->
        def participants = sql.rows("""
			SELECT         pd.participant_date_id, u.user_id, u.lastname, u.firstname, u.email, u.organisation, 
                           CAST(GROUP_CONCAT(DISTINCT pt.type ORDER BY pt.importance DESC SEPARATOR ', ') AS CHAR) as types
            FROM           session_participant sp
            INNER JOIN     users AS u
            ON             sp.user_id = u.user_id            
            INNER JOIN     participant_types AS pt
            ON             sp.participant_type_id = pt.participant_type_id
            INNER JOIN     participant_date AS pd
            ON             u.user_id = pd.user_id           
            WHERE          sp.session_id = :sessionId
            AND            u.deleted = 0
            AND            pd.deleted = 0
            AND            pd.date_id = :dateId           
            GROUP BY       u.user_id
            ORDER BY       pt.importance DESC, u.lastname ASC, u.firstname ASC 
		""", ['sessionId': session['session_id'], 'dateId': params.dateId])

        participants = participants?.findAll {
            extraParticipantIds.contains(it['participant_date_id'].toLong())
        }

        def text = ""
        if (participants?.size() > 0) {
            def participantsText = participants.collect { participant ->
                noSessionInfo = false

                def participantText = """
                    |First name: ${participant['firstname']}
                    |Last name: ${participant['lastname']}
                    |Email: ${participant['email']}
					|Organisation: ${(participant['organisation']) ? participant['organisation'].trim() : '-'}
                    |Type(s) in session: ${participant['types']}					
                 """.stripMargin().trim()

                papers = sql.rows("""
					SELECT		p.title, p.co_authors, p.abstract
					FROM 		papers p					
					WHERE 		p.user_id = :userId 
					AND 		p.date_id = :dateId
					AND 		p.session_id = :sessionId
					AND 		p.deleted = 0
					ORDER BY 	p.title ASC
				""", ['sessionId': session['session_id'], 'dateId': params.dateId, 'userId': participant['user_id']])

                papers?.each { paper ->
                    participantText += "\n\n"
                    participantText += """
						|Paper title: ${paper['title']}
						|Co-author(s): ${(paper['co_authors']) ? paper['co_authors']?.trim() : '-'}
						|Paper abstract:
						|${paper['abstract']}
					""".stripMargin().trim()
                }

                participantText
            }

            text += """
				|Session: ${session['session_name']}
				|Abstract: 
				|${(session['session_abstract']) ? session['session_abstract'].trim() : '-'}
			""".stripMargin().trim()

            text += shortMailSeparator
            text += participantsText.join(shortMailSeparator)
        }

        text
    }

    sessionsInfo = sessionsInfo.findAll { !it.isAllWhitespace() }
    sessionsText = sessionsInfo.join(longMailSeparator)
}

// INDIVIDUAL PAPERS

def participants = sql.rows("""
	SELECT DISTINCT	pd.participant_date_id, u.user_id, u.lastname, u.firstname, u.email, u.organisation
	FROM 			users u
	INNER JOIN 		papers AS p	
	ON        		p.user_id = u.user_id      
	INNER JOIN 		participant_date AS pd
	ON         		u.user_id = pd.user_id   	
	WHERE 			p.date_id = :dateId
	AND 			p.session_id IS NULL
	AND 			p.network_proposal_id = :networkId
	AND 			p.deleted = 0
	AND       		u.deleted = 0
	AND        		pd.deleted = 0
	AND    			pd.date_id = :dateId
	ORDER BY 		u.lastname ASC, u.firstname ASC 
""", params)

participants = participants.findAll {
    extraParticipantIds.contains(it['participant_date_id'].toLong())
}

if (participants?.size() > 0) {
    papersText += "INDIVIDUAL PAPER PROPOSALS \n\n"

    def participantsText = participants.collect { participant ->
        noPaperInfo = false

        def participantText = """
			|First name: ${participant['firstname']}
			|Last name: ${participant['lastname']}
			|Email: ${participant['email']}
			|Organisation: ${(participant['organisation']) ? participant['organisation'].trim() : '-'}
		 """.stripMargin().trim()

        papers = sql.rows("""
			SELECT		p.title, p.co_authors, p.abstract
			FROM 		papers p					
			WHERE 		p.user_id = :userId 
			AND 		p.date_id = :dateId
			AND 		p.session_id IS NULL
			AND 		p.network_proposal_id = :networkId
			AND 		p.deleted = 0
			ORDER BY 	p.title ASC
		""", ['networkId': params.networkId, 'dateId': params.dateId, 'userId': participant['user_id']])

        papers?.each { paper ->
            participantText += "\n"
            participantText += """
				|Paper title: ${paper['title']}
				|Co-author(s): ${(paper['co_authors']) ? paper['co_authors']?.trim() : '-'}
				|Paper abstract:
				|${paper['abstract']}
			""".stripMargin().trim()
        }

        participantText
    }

    papersText += participantsText.join(shortMailSeparator)
}

if (!noSessionInfo || !noPaperInfo) {
    if (!noSessionInfo && !noPaperInfo) {
        sessionsText + longMailSeparator + papersText
    }
    else {
        sessionsText + papersText
    }
}
else {
    "There were no new registrations for your network."
}
