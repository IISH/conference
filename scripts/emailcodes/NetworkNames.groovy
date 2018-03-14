def networks = sql.rows("""
    SELECT n.name
    FROM papers p
    LEFT JOIN networks n
    ON p.network_proposal_id = n.network_id
    WHERE p.user_id = :userId
    AND p.date_id = :dateId
""", params)

if (networks?.size() > 0) {
    networks.collect { it['name'] }.join(', ').replaceAll('null', '-')
}
else {
    "-"
}