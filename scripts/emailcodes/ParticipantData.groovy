def personalData = sql.rows("""
    SELECT         u.firstname, u.lastname, u.gender, u.organisation, 
                   u.department, u.email, u.city, 
                   c.name_english, u.phone, u.mobile, pd.student, u.cv
    FROM           users AS u
    INNER JOIN     participant_date AS pd
    ON             u.user_id = pd.user_id
    LEFT JOIN      countries AS c
    ON             u.country_id = c.country_id
    WHERE          u.user_id = :userId
    AND            u.deleted = 0
    AND            pd.date_id = :dateId
    AND            pd.deleted = 0
""", params)

def extras = sql.rows("""
    SELECT         e.description
    FROM           participant_date AS pd  
	LEFT JOIN      participant_date_extra AS pde
    ON             pde.participant_date_id = pd.participant_date_id  
	LEFT JOIN      extras AS e
    ON             e.extra_id = pde.extra_id 
    WHERE          pd.date_id = :dateId
    AND            pd.deleted = 0
	AND 		   pd.user_id = :userId
	AND			   e.is_final_registration = 0
	AND            e.date_id = :dateId
	AND 		   e.deleted = 0
""", params)

if (personalData?.size() == 1) {
    def gender = '-'
    if (personalData[0]['gender']) {
        gender = (personalData[0]['gender'] == 'M') ? 'Male' : 'Female'
    }

    String data = """ 
        |First name: ${personalData[0]['firstname']}
        |Last name: ${personalData[0]['lastname']}
        |Gender: ${gender}
        |Organisation: ${(personalData[0]['organisation']) ? personalData[0]['organisation'] : '-'}
        |Department: ${(personalData[0]['department']) ? personalData[0]['department'] : '-'}
    """.trim().stripMargin()

    if (getValueForSetting('show_student')?.toInteger() == 1) {
        data += "\n(PhD) Student?: ${(personalData[0]['student']) ? 'yes' : 'no'}"
    }

    data += "\n" + """
        |Email: ${(personalData[0]['email']) ? personalData[0]['email'] : '-'}
        |City: ${(personalData[0]['city']) ? personalData[0]['city'] : '-'}
        |Country: ${(personalData[0]['name_english']) ? personalData[0]['name_english'] : '-'}
        |Phone number: ${(personalData[0]['phone']) ? personalData[0]['phone'] : '-'}
        |Mobile number: ${(personalData[0]['mobile']) ? personalData[0]['mobile'] : '-'}
     """.trim().stripMargin()

    if (getValueForSetting('show_cv')?.toInteger() == 1) {
        data += "\nCurriculum Vitae: ${(personalData[0]['cv']) ? personalData[0]['cv'] : '-'}"
    }

    if (extras?.size() > 0) {
        data += "\n\n" + extras.collect { "- ${it['description'].trim()}" }.join('\n')
    }

    data
}
else {
    '-'
}