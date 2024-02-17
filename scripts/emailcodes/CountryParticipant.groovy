def result = sql.rows("""
	SELECT c.name_english 
	FROM users u
	INNER JOIN countries AS c
	ON u.country_id = c.country_id
	WHERE user_id = :userId
""", params)

if (result?.size() == 1) {
    result[0]['name_english']
}
else {
    '-'
}