def sessions = null

if (params.containsKey('sessionId')) {
    sessions = sql.rows("""
        SELECT DISTINCT    s.session_id, s.session_name
        FROM               sessions s
        INNER JOIN         session_participant AS sp
        ON                 s.session_id = sp.session_id
        INNER JOIN         users AS u
        ON                 sp.user_id = u.user_id
        WHERE              s.date_id = :dateId
        AND                s.deleted = 0
        AND                u.user_id = :userId
        AND                s.session_id = :sessionId
        ORDER BY           s.session_name ASC
    """, params)
}
else {
    sessions = sql.rows("""
        SELECT DISTINCT    s.session_id, s.session_name
        FROM               sessions s
        INNER JOIN         session_participant AS sp
        ON                 s.session_id = sp.session_id
        INNER JOIN         users AS u
        ON                 sp.user_id = u.user_id
        WHERE              s.date_id = :dateId
        AND                s.session_state_id = 2
        AND                s.deleted = 0
        AND                u.user_id = :userId
        ORDER BY           s.session_name ASC
    """, params)
}

if (sessions?.size() > 0) {
    def sessionsInfo = sessions.collect { session ->
        def participants = sql.rows("""
            SELECT        pt.type, u.lastname, u.firstname, u.email
            FROM          session_participant sp
            INNER JOIN    users AS u
            ON            sp.user_id = u.user_id
            INNER JOIN    participant_types AS pt
            ON            sp.participant_type_id = pt.participant_type_id
            INNER JOIN    participant_date AS pd
            ON            u.user_id = pd.user_id
            WHERE         sp.session_id = :sessionId
            AND           u.deleted = 0
            AND           pd.deleted = 0
            AND           pd.date_id = :dateId
            AND           pd.participant_state_id = 2
            ORDER BY      pt.importance DESC, u.lastname ASC, u.firstname ASC 
        """, ['sessionId': session['session_id'], 'dateId': params.dateId])

        String text = session['session_name'].trim()

        if (participants?.size() > 0) {
            participants.each { participant ->
                text += "\n- ${participant['type']}: ${participant['firstname']} ${participant['lastname']} (${participant['email']})"
            }
        }
        else {
            text += "\nNo session members"
        }

        text
    }

    sessionsInfo.join('\n\n')
}
else {
    "-"
}