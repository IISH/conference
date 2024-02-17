def network = sql.rows("""
	SELECT	n.name
	FROM 	networks n	
	WHERE 	n.network_id = :networkId
""", params)

if (network?.size() == 1) {
    network[0]['name']
}
else {
    "-"
}
