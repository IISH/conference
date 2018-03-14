def sessions = sql.rows("""
    SELECT        s.session_name AS session, 
                  GROUP_CONCAT(DISTINCT pt.type SEPARATOR ', ') AS types, 
                  GROUP_CONCAT(DISTINCT n.name SEPARATOR ', ') AS networks, 
                  p.title AS paper,
                  COUNT(n.network_id) AS count_networks
    FROM          session_participant sp
    INNER JOIN    sessions AS s
    ON            sp.session_id = s.session_id    
    INNER JOIN    participant_types AS pt
    ON            sp.participant_type_id = pt.participant_type_id
    INNER JOIN    session_in_network sin
    ON            s.session_id = sin.session_id            
    INNER JOIN    networks AS n
    ON            sin.network_id = n.network_id
    LEFT JOIN     papers AS p
    ON            s.session_id = p.session_id
    AND           p.user_id = :userId  
    WHERE         sp.user_id = :userId   
    AND           s.date_id = :dateId
    AND           s.deleted = 0    
    AND           s.session_state_id = 2
    AND           n.date_id = :dateId
    AND           n.deleted = 0
    AND           n.show_online = 1
    AND (
        p.paper_id IS NULL 
        OR (
            p.date_id = :dateId
            AND p.deleted = 0    
            AND p.paper_state_id = 2
            AND p.user_id = :userId  
        )
    )
    GROUP BY      s.session_id
""", params)

if (sessions?.size() > 0) {
    def lines = []

    for (def session : sessions) {
        String line = "- Session \"${session['session'].trim()}\" as ${session['types'].toLowerCase()} "
        if (session['paper']) {
            line += "(Paper: \"${session['paper'].trim()}\") ";
        }

        if (getValueForSetting('show_network')?.toInteger() == 1) {
            if (session['count_networks'] > 1) {
                String networkName = getValueForSetting('network_name_singular')
                line += "(${networkName}: ${session['networks']}) "
            }
            else {
                String networksName = getValueForSetting('network_name_plural')
                line += "(${networksName}: ${session['networks']}) "
            }
        }

        lines << line
    }

    lines.join('\r\n')
}
else {
    "-"
}