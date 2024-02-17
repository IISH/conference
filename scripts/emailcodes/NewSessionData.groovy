def sessions = null

String sessionName = getValueForSetting('session_name_singular')

if (params.containsKey('sessionId')) {
    sessions = sql.rows("""
        SELECT            s.session_id, s.session_name, s.session_abstract
        FROM              sessions s   
        WHERE             s.date_id = :dateId 
        AND               s.session_id = :sessionId
        AND               s.deleted = 0
    """, params)
}
else {
    sessions = sql.rows("""
        SELECT DISTINCT   s.session_id, s.session_name, s.session_abstract                          
        FROM              sessions s
        
        INNER JOIN        session_participant sp
        ON                s.session_id = sp.session_id
        
        WHERE             s.date_id = :dateId       
        AND               s.deleted = 0
        AND               sp.added_by = :addedByUserId
    """, params)
}

if (sessions?.size() > 0) {
    String text = ""

    sessions.collect { session ->
        def participants = sql.rows("""
            SELECT             u.lastname, u.firstname, u.email, pd.student, p.title, p.abstract, 
                               CAST(GROUP_CONCAT(pt.type ORDER BY pt.importance DESC SEPARATOR ', ') AS CHAR) as types
            FROM               session_participant sp
            
            INNER JOIN         users AS u 
            ON                 sp.user_id = u.user_id    
                    
            INNER JOIN         participant_types AS pt 
            ON                 sp.participant_type_id = pt.participant_type_id
            
            INNER JOIN         participant_date AS pd 
            ON                 u.user_id = pd.user_id
            
            LEFT JOIN          papers AS p 
            ON                 u.user_id = p.user_id 
            AND                (p.session_id = :sessionId OR p.paper_id IS NULL)
            
            WHERE              sp.session_id = :sessionId
            AND                sp.date_id = :dateId 
            AND                sp.added_by = :addedByUserId
            AND                u.deleted = 0          
            AND                pd.deleted = 0
            AND                pd.date_id = :dateId
            AND                (p.date_id = :dateId OR p.date_id IS NULL)
            AND                (p.deleted = 0 OR p.deleted IS NULL)
            GROUP BY           u.user_id
            ORDER BY           u.lastname ASC, u.firstname ASC
        """, ['sessionId': session['session_id'], 'addedByUserId': params.addedByUserId, 'dateId': params.dateId])

        String participantsText = ""
        if (participants?.size() > 0) {
            participants.each { participant ->
                participantsText += """                  
                    |First name: ${participant['firstname']}
                    |Last name: ${participant['lastname']}
                    |Email: ${participant['email']}
                 """.stripMargin().trim()

                if (getValueForSetting('show_student')?.toInteger() == 1) {
                    participantsText += "\n(PhD) Student?: ${(participant['student']) ? 'yes' : 'no'}"
                }

                participantsText += "\nType(s) in ${sessionName}: ${participant['types']}"

                if (participant['title']) {
                    participantsText += "\n" + """
                         |Paper title: ${participant['title']}
                         |Paper abstract:
                         |${participant['abstract'] ? participant['abstract'] : '-'}
                     """.stripMargin().trim()
                }

                participantsText += "\n\n"
            }
        }
        else {
            participantsText = "No ${sessionName} members \n\n"
        }

        text += """
            |${sessionName}: ${session['session_name']}
            |Abstract:
            |${session['session_abstract'] ? session['session_abstract'] : '-'}
            |
            |Participants:
            |
            |${participantsText}
        """.stripMargin().trim()

        text += "\n\n\n"
    }

    text.trim()
}
else {
    "No ${sessionName}(s)"
}