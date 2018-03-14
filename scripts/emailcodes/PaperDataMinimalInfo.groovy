def papers = null

if (params.containsKey('paperId')) {
    papers = sql.rows("""
        SELECT       p.title, p.co_authors, ps.description
        FROM         papers p
        LEFT JOIN    paper_states AS ps
        ON           p.paper_state_id = ps.paper_state_id        
        WHERE        p.user_id = :userId 
        AND          p.date_id = :dateId
        AND          p.deleted = 0
        AND          p.paper_id = :paperId
        GROUP BY     p.paper_id
    """, params)
}
else {
    papers = sql.rows("""
        SELECT       p.title, p.co_authors, ps.description
        FROM         papers p
        LEFT JOIN    paper_states AS ps
        ON           p.paper_state_id = ps.paper_state_id        
        WHERE        p.user_id = :userId 
        AND          p.date_id = :dateId
        AND          p.deleted = 0
        GROUP BY     p.paper_id
    """, params)
}

if (papers?.size() > 0) {
    String text = ""

    papers.each { paper ->
        text += """
            |Paper title: ${paper['title']}
            |Paper state: ${paper['description']}
            |Co-author(s): ${(paper['co_authors']) ? paper['co_authors']?.trim() : '-'}
        """.stripMargin().trim()

        text += "\n\n"
    }

    text.trim()
}
else {
    "-"
}