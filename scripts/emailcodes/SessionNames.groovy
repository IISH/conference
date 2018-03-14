if (params.containsKey('sessionId')) {
    def sessions = sql.rows("""
        SELECT session_name
        FROM sessions
        WHERE date_id = :dateId
        AND deleted = 0
        AND session_id = :sessionId
    """, params)

    if (sessions?.size() > 0) {
        sessions[0]['session_name'].trim()
    }
    else {
        '-'
    }
}
else {
    def sessions = sql.rows("""
        SELECT DISTINCT    s.session_id, s.session_name
        FROM            sessions s
        INNER JOIN        session_participant AS sp
        ON                s.session_id = sp.session_id
        INNER JOIN        users AS u
        ON                sp.user_id = u.user_id
        WHERE             s.date_id = :dateId
        AND                s.deleted = 0
        AND                s.session_state_id = 2
        AND                sp.deleted = 0
        AND             u.user_id = :userId
        ORDER BY        s.session_name ASC
    """, params)

    if (sessions?.size() > 0) {
        sessions.collect { "- ${it['session_name'].trim()}" }.join('\n')
    }
    else {
        '-'
    }
}