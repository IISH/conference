package org.iisg.eca

import java.text.SimpleDateFormat
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib

/**
 * Domain class of table holding all event days
 */
class Day extends DefaultDomain {
    private static final ValidationTagLib MESSAGES = new ValidationTagLib()

    Date day
    int dayNumber
    EventDate date

    static belongsTo = EventDate

    static constraints = {
        dayNumber   min: 0
    }

    static mapping = {
        table 'days'
 		version false

        id          column: 'day_id'
        day         column: 'day'
        dayNumber   column: 'day_number'
        date        column: 'date_id'
	}

    @Override
    String toString() {
        def dateFormat = MESSAGES.message(code: 'default.date.format').toString()
        def sdf = new SimpleDateFormat(dateFormat)
        "${dayNumber}: ${sdf.format(day)}"
    }
}
