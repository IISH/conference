def papers = null

if (params.containsKey('paperId')) {
    papers = sql.rows("""
		SELECT title
		FROM papers
		WHERE user_id = :userId
		AND date_id = :dateId
		AND paper_id = :paperId
		AND deleted = 0
	""", params)
}
else {
    papers = sql.rows("""
		SELECT title
		FROM papers
		WHERE user_id = :userId
		AND date_id = :dateId
		AND deleted = 0
	""", params)
}

if (papers?.size() > 0) {
    def paper = papers.first()
    paper['title'].trim()
}
else {
    "-"
}