def sessions = null

if (params.containsKey('sessionId')) {
    sessions = sql.rows("""
        SELECT            s.session_id, s.session_name, s.session_abstract, ss.description
        FROM              sessions s  
INNER JOIN session_states AS ss
ON s.session_state_id = ss.session_state_id
        WHERE             s.date_id = :dateId
        AND               s.session_id = :sessionId
        AND               s.deleted = 0
    """, params)
}

if (sessions?.size() > 0) {
    String text = ""

    sessions.collect { session ->
        def participants = sql.rows("""
            SELECT             u.lastname, u.firstname, u.email, u.organisation, p.title, ps.description,
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
           
LEFT JOIN paper_states AS ps
ON p.paper_state_id = ps.paper_state_id

            WHERE              sp.session_id = :sessionId
            AND                sp.date_id = :dateId
            AND                u.deleted = 0         
            AND                pd.deleted = 0
            AND                pd.date_id = :dateId
            AND                (p.date_id = :dateId OR p.date_id IS NULL)
            AND                (p.deleted = 0 OR p.deleted IS NULL)
            GROUP BY           u.user_id
            ORDER BY           u.lastname ASC, u.firstname ASC
        """, ['sessionId' : session['session_id'], 'dateId' : params.dateId])

        String participantsText = ""
        if (participants?.size() > 0) {
            participants.each { participant ->
                participantsText += """                 
                    |Participant name: ${participant['firstname']} ${participant['lastname']} (${participant['email']})
                    |Organisation: ${participant['organisation'] ? participant['organisation'] : '-'}
                    |Function: ${participant['types']}
                 """.stripMargin().trim()

                if (participant['title']) {
                    participantsText += "\n" + """
                         |Paper title: ${participant['title']}
                         |Paper state: ${participant['description']}
                     """.stripMargin().trim()
                }

                participantsText += "\n\n"
            }
        }
        else {
            participantsText = "No session members \n\n"
        }

        def networks = sql.rows("""
            SELECT networks.network_id, networks.name, users.user_id, users.firstname, users.lastname, users.email
FROM networks
	INNER JOIN session_in_network ON networks.network_id = session_in_network.network_id
	INNER JOIN networks_chairs ON networks.network_id = networks_chairs.network_id
	INNER JOIN users ON networks_chairs.user_id = users.user_id
WHERE networks.date_id = :dateId
	AND session_in_network.session_id = :sessionId
	AND networks.deleted = 0
	AND networks.show_online = 1
	AND networks_chairs.deleted = 0
	AND users.deleted = 0

        """, ['sessionId' : session['session_id'], 'dateId' : params.dateId])

        String networksText = ""
        if (networks?.size() > 0) {
            def lastNetworkName = null
            networks.each { network ->
                if (network['name'] != lastNetworkName) {
                    networksText += "\n" + """
                         |Network: ${network['name']}
                         |Network chair(s):
                     """.stripMargin().trim()
                }

                networksText += "\n- ${network['firstname']} ${network['lastname']} (${network['email']})"
                lastNetworkName = network['name']
            }
        }

        text += """
            |
            |Session Info 
            |- - - - - - - - - 
            |
            |Session proposal: ${session['session_name']}
            |Session state: ${session['description']}
            |
            |${networksText}
            |
            |Participants:
            |${participantsText}
        """.stripMargin().trim()

        text += "\n\n\n"
    }

    text.trim()
}
else {
    "No session(s)"
}
