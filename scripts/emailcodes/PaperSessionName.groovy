def sessions = null

if (params.containsKey('paperId')) {
    sessions = sql.rows("""
		SELECT s.session_name
		FROM papers p 		
		INNER JOIN sessions AS s
		ON p.session_id = s.session_id		
		WHERE p.user_id = :userId	
		AND p.date_id = :dateId
		AND p.deleted = 0		
		AND p.paper_id = :paperId
		AND s.date_id = :dateId
		AND s.deleted = 0
	""", params)
}
else {
    sessions = sql.rows("""
		SELECT s.session_name
		FROM papers p 		
		INNER JOIN sessions AS s
		ON p.session_id = s.session_id		
		WHERE p.user_id = :userId	
		AND p.date_id = :dateId
		AND p.deleted = 0		
		AND s.date_id = :dateId
		AND s.deleted = 0
	""", params)
}

if (sessions?.size() > 0) {
    def session = sessions.first()
    session['session_name'].trim()
}
else {
    "-"
}