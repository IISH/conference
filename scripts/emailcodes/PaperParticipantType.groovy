def types = null

if (params.containsKey('paperId')) {
    types = sql.rows("""
		SELECT CAST(GROUP_CONCAT(DISTINCT pt.type SEPARATOR ', ') AS CHAR) AS types
		FROM session_participant sp
		INNER JOIN sessions AS s
		ON sp.session_id = s.session_id	
		INNER JOIN participant_types AS pt
		ON sp.participant_type_id = pt.participant_type_id
		INNER JOIN papers AS p
		ON s.session_id = p.session_id
		WHERE sp.user_id = :userId
		AND s.date_id = :dateId
		AND s.deleted = 0	
		AND p.date_id = :dateId
		AND p.deleted = 0	
		AND p.user_id = :userId  
		AND p.paper_id = :paperId
	""", params)
}
else {
    types = sql.rows("""
		SELECT CAST(GROUP_CONCAT(DISTINCT pt.type SEPARATOR ', ') AS CHAR) AS types
		FROM session_participant sp
		INNER JOIN sessions AS s
		ON sp.session_id = s.session_id	
		INNER JOIN participant_types AS pt
		ON sp.participant_type_id = pt.participant_type_id
		INNER JOIN papers AS p
		ON s.session_id = p.session_id
		WHERE sp.user_id = :userId  		
		AND s.date_id = :dateId
		AND s.deleted = 0	
		AND p.date_id = :dateId
		AND p.deleted = 0	
		AND p.user_id = :userId
	""", params)
}

if (types?.size() > 0) {
    def type = types.first()
    (type['types']) ? type['types'].trim().toLowerCase() : '-'
}
else {
    "-"
}