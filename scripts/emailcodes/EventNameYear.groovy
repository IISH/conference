def result = sql.rows("SELECT short_name, year_code FROM events INNER JOIN dates WHERE events.event_id = dates.event_id AND date_id = :dateId", params)

if (result?.size() == 1) {
    def values = result[0].values()
    values.removeAll(['null', null])
    values.join(' ')
}
else {
    '-'
}