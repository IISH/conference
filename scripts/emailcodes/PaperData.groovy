def papers = null

if (params.containsKey('paperId')) {
    papers = sql.rows("""
        SELECT        p.title, p.abstract, p.co_authors, ps.description, s.session_name, s.date_id AS s_date_id,
                      s.deleted AS s_deleted, n.name, n.date_id AS n_date_id, n.deleted AS n_deleted,
                      p.equipment_comment, p.review_comment,
                      CAST(GROUP_CONCAT(DISTINCT CONCAT(u.firstname, ' ', u.lastname) SEPARATOR ', ') AS CHAR) AS chairs,
                      CAST(GROUP_CONCAT(DISTINCT e.equipment SEPARATOR ', ') AS CHAR) AS equipment
        FROM          papers p
        LEFT JOIN     paper_states AS ps
        ON            p.paper_state_id = ps.paper_state_id
        LEFT JOIN     sessions AS s
        ON            p.session_id = s.session_id
        LEFT JOIN     session_in_network AS sin
        ON            s.session_id = sin.session_id
        LEFT JOIN     networks AS n
        ON            sin.network_id = n.network_id
        LEFT JOIN     networks_chairs AS nc
        ON            nc.network_id = n.network_id
        LEFT JOIN     users AS u
        ON            nc.user_id = u.user_id
        LEFT JOIN     paper_equipment AS pe
        ON            p.paper_id = pe.paper_id
        LEFT JOIN     equipment AS e
        ON            pe.equipment_id = e.equipment_id
        WHERE         p.user_id = :userId
        AND           p.date_id = :dateId
        AND           p.deleted = 0
        AND           p.paper_id = :paperId
        GROUP BY      p.paper_id
    """, params)
}
else {
    papers = sql.rows("""
        SELECT        p.title, p.abstract, p.co_authors, ps.description, s.session_name, s.date_id AS s_date_id,
                      s.deleted AS s_deleted, n.name, n.date_id AS n_date_id, n.deleted AS n_deleted,
                      p.equipment_comment, p.review_comment,
                      CAST(GROUP_CONCAT(DISTINCT CONCAT(u.firstname, ' ', u.lastname) SEPARATOR ', ') AS CHAR) AS chairs,
                      CAST(GROUP_CONCAT(DISTINCT e.equipment SEPARATOR ', ') AS CHAR) AS equipment
        FROM          papers p
        LEFT JOIN     paper_states AS ps
        ON            p.paper_state_id = ps.paper_state_id
        LEFT JOIN     sessions AS s
        ON            p.session_id = s.session_id
        LEFT JOIN     session_in_network AS sin
        ON            s.session_id = sin.session_id
        LEFT JOIN     networks AS n
        ON            sin.network_id = n.network_id
        LEFT JOIN     networks_chairs AS nc
        ON            nc.network_id = n.network_id
        LEFT JOIN     users AS u
        ON            nc.user_id = u.user_id
        LEFT JOIN     paper_equipment AS pe
        ON            p.paper_id = pe.paper_id
        LEFT JOIN     equipment AS e
        ON            pe.equipment_id = e.equipment_id
        WHERE         p.user_id = :userId
        AND           p.date_id = :dateId
        AND           p.deleted = 0
        GROUP BY      p.paper_id
    """, params)
}

if (papers?.size() > 0) {
    String text = ""

    papers.each { paper ->
        boolean sessionExists = (paper['session_name'] && (paper['s_date_id'].toLong() == params.dateId) && !paper['s_deleted'])
        boolean networkExists = (paper['name'] && (paper['n_date_id'].toLong() == params.dateId) && !paper['n_deleted'])

        text += """
            |Paper title: ${paper['title']}
            |Paper state: ${paper['description']}
            |Co-author(s): ${(paper['co_authors']) ? paper['co_authors']?.trim() : '-'}
            |Session: ${(sessionExists) ? paper['session_name'] : '-'}
        """.stripMargin().trim()

        if (getValueForSetting('show_network')?.toInteger() == 1) {
            String networkName = getValueForSetting('network_name_singular')

            text += "\n" + """
                |${networkName}: ${(networkExists) ? paper['name'] : '-'}
                |Chairs in ${networkName.toLowerCase()}: ${(networkExists && paper['chairs']) ? paper['chairs'] : '-'}
            """.stripMargin().trim()
        }

        text += "\n" + """
            |Equipment: ${(paper['equipment']) ? paper['equipment'] : '-'}
            |Equipment request: ${(paper['equipment_comment']) ? paper['equipment_comment'] : '-'}
            |
            |Abstract:
            |${paper.abstract}
        """.stripMargin().trim()

        if (getValueForSetting('enable_paper_reviews')?.toInteger() == 1) {
            def reviews = sql.rows("""
                SELECT        pr.paper_review_id AS id, pr.review, prs.score, rc.name
                FROM          paper_reviews pr
                LEFT JOIN     paper_review_scores AS prs
                ON            pr.paper_review_id = prs.paper_review_id
                LEFT JOIN     review_criteria AS rc
                ON            prs.review_criteria_id = rc.review_criteria_id
                WHERE         pr.paper_id = :paperId
                AND           pr.date_id = :dateId
                AND           prs.score IS NOT NULL
                ORDER BY      pr.paper_review_id, rc.sort_order ASC
            """, params)

            if (reviews?.size() > 0) {
                def reviewText = null, reviewId = null, count = 1
                reviews.each { review ->
                    if (reviewId != review.id) {
                        if (reviewId != null) {
                            text += "\n" + """
                                |Review:
                                |${reviewText}
                            """.stripMargin().trim()
                        }

                        reviewId = review.id
                        reviewText = review.review

                        text += "\n\n" + "Review #" + count++
                    }

                    text += "\n" + "${review.name}: ${review.score}".trim()
                }

                if (reviewText) {
                    text += "\n" + """
                        |Review:
                        |${reviewText}
                    """.stripMargin().trim()
                }

                if (paper['review_comment']) {
                    text += "\n\n" + """
                        |Editor comment:
                        |${paper['review_comment']}
                    """.stripMargin().trim()
                }
            }
        }

        text += "\n\n"
    }

    text.trim()
}
else {
    "-"
}