def result = sql.rows("SELECT firstname, lastname FROM users WHERE user_id = :userId", params)

if (result?.size() == 1) {
    def values = result[0].values()
    values.removeAll(['null', null])
    values.join(' ')
}
else {
    '-'
}