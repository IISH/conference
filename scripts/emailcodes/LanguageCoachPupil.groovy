def languageCoachNetworks = sql.rows("""
        SELECT         n.name
        FROM           participant_volunteering pv
        INNER JOIN     networks AS n
        ON             pv.network_id = n.network_id
        INNER JOIN     participant_date AS pd
        ON             pv.participant_date_id = pd.participant_date_id
        INNER JOIN     users AS u
        ON             pd.user_id = u.user_id
        WHERE          u.user_id = :userId
        AND            u.deleted = 0
        AND            pd.date_id = :dateId
        AND            pd.deleted = 0
        AND            pv.volunteering_id = 3
        GROUP BY       n.network_id
    """, params)

def languagePupilNetworks = sql.rows("""
        SELECT         n.name
        FROM           participant_volunteering pv
        INNER JOIN     networks AS n
        ON             pv.network_id = n.network_id
        INNER JOIN     participant_date AS pd
        ON             pv.participant_date_id = pd.participant_date_id
        INNER JOIN     users AS u
        ON             pd.user_id = u.user_id
        WHERE          u.user_id = :userId
        AND            u.deleted = 0
        AND            pd.date_id = :dateId
        AND            pd.deleted = 0
        AND            pv.volunteering_id = 4
        GROUP BY       n.network_id
    """, params)

def text = "I would like to be an English Language Coach: \n"
if (languageCoachNetworks?.size() > 0) {
    text += 'Yes \n'

    if (getValueForSetting('show_network')?.toInteger() == 1) {
        String networkName = getValueForSetting('network_name_plural')
        text += "${networkName}: ${languageCoachNetworks*.name.join(', ')} \n"
    }
}
else {
    text += 'No \n'
}

text += "\n"
text += "I need some help from an English Language Coach: \n"
if (languagePupilNetworks?.size() > 0) {
    text += 'Yes \n'
    if (getValueForSetting('show_network')?.toInteger() == 1) {
        String networkName = getValueForSetting('network_name_plural')
        text += "${networkName}: ${languagePupilNetworks*.name.join(', ')} \n"
    }
}
else {
    text += 'No \n'
}

text