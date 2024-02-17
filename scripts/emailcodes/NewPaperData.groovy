def papers = null

String sessionName = getValueForSetting('session_name_singular')

if (params.containsKey('paperId')) {
    papers = sql.rows("""
        SELECT        p.title, p.abstract, p.co_authors, p.session_proposal, n.name, n.date_id AS n_date_id, 
                      n.deleted AS n_deleted, p.equipment_comment, pd.award, s.session_name as session_name,
                      CAST(GROUP_CONCAT(DISTINCT e.equipment SEPARATOR ', ') AS CHAR) AS equipment
        FROM          papers p    
        LEFT JOIN     networks AS n
        ON            p.network_proposal_id = n.network_id
        LEFT JOIN     participant_date AS pd
        ON            p.user_id = pd.user_id
        LEFT JOIN     paper_equipment AS pe
        ON            p.paper_id = pe.paper_id
        LEFT JOIN     equipment AS e
        ON            pe.equipment_id = e.equipment_id
        LEFT JOIN     sessions AS s
        ON            p.session_id = s.session_id
        WHERE         p.user_id = :userId 
        AND           p.date_id = :dateId
        AND           p.deleted = 0
        AND           p.paper_id = :paperId
        AND           pd.date_id = :dateId
        AND           pd.deleted = 0
        AND           (s.date_id = :dateId OR s.date_id IS NULL)
        AND           (s.deleted = 0 OR s.deleted IS NULL)
        GROUP BY      p.paper_id
    """, params)
}
else {
    papers = sql.rows("""
        SELECT        p.title, p.abstract, p.co_authors, p.session_proposal, n.name, n.date_id AS n_date_id, 
                      n.deleted AS n_deleted, p.equipment_comment, pd.award, s.session_name as session_name,
                      CAST(GROUP_CONCAT(DISTINCT e.equipment SEPARATOR ', ') AS CHAR) AS equipment
        FROM          papers p    
        LEFT JOIN     networks AS n
        ON            p.network_proposal_id = n.network_id
        LEFT JOIN     participant_date AS pd
        ON            p.user_id = pd.user_id
        LEFT JOIN     paper_equipment AS pe
        ON            p.paper_id = pe.paper_id
        LEFT JOIN     equipment AS e
        ON            pe.equipment_id = e.equipment_id
        LEFT JOIN     sessions AS s
        ON            p.session_id = s.session_id
        WHERE         p.user_id = :userId 
        AND           p.date_id = :dateId
        AND           p.deleted = 0
        AND           pd.date_id = :dateId
        AND           pd.deleted = 0
        AND           (s.date_id = :dateId OR s.date_id IS NULL)
        AND           (s.deleted = 0 OR s.deleted IS NULL)
        GROUP BY      p.paper_id
    """, params)
}

if (papers?.size() > 0) {
    String text = ""

    papers.each { paper ->
        boolean networkExists = (paper['name'] && (paper['n_date_id'].toLong() == params.dateId) && !paper['n_deleted'])

        text += """ 
            |Paper title: ${paper['title']}
            |Co-author(s): ${(paper['co_authors']) ? paper['co_authors']?.trim() : '-'}
        """.stripMargin().trim()

        if (getValueForSetting('preregistration_sessions')?.toInteger() == 1) {
            text += "\nProposed ${sessionName}: ${(paper['session_name']) ? paper['session_name'] : '-'}"
        }
        else {
            if (getValueForSetting('show_network')?.toInteger() == 1) {
                String networkName = getValueForSetting('network_name_singular')

                text += "\nProposed ${networkName.toLowerCase()}: ${(networkExists) ? paper['name'] : '-'}"
            }

            text += "\nProposed ${sessionName}: ${(paper['session_proposal']) ? paper['session_proposal'] : '-'}"
        }

        if (getValueForSetting('show_award')?.toInteger() == 1) {
            text += "\nAward: ${(paper['award']) ? 'yes' : 'no'}"
        }

        if (getValueForSetting('show_equipment')?.toInteger() == 1) {
            text += "\n" + """           
                |Audio/visual equipment: ${(paper['equipment']) ? paper['equipment'] : '-'}
                |Extra audio/visual request: ${(paper['equipment_comment']) ? paper['equipment_comment'] : '-'}
                |
                |Abstract:
                |${paper.abstract}
            """.stripMargin().trim()
        }

        text += "\n\n"
    }

    text.trim()
}
else {
    "No paper(s)"
}