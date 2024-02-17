def result = sql.rows("SELECT extra_info FROM participant_date WHERE user_id = :userId AND date_id = :dateId AND extra_info IS NOT NULL", params)

if (result?.size() == 1) {
    result[0]['extra_info']
}
else {
    '-'
}