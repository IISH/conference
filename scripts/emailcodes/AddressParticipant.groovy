def result = sql.rows("SELECT address FROM users WHERE user_id = :userId", params)

if (result?.size() == 1) {
    result[0]['address']
}
else {
    '-'
}