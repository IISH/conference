package org.iisg.eca.domain

import java.text.SimpleDateFormat
import org.springframework.context.i18n.LocaleContextHolder

/**
 * Domain class of table holding all event days
 */
class Day extends EventDateDomain {
    def messageSource

    Date day
    int dayNumber
    
    static hasMany = [sessionDateTimes: SessionDateTime]
    
    static constraints = {
        dayNumber   min: 0
    }

    static mapping = {
        table 'days'
        version false

        id          column: 'day_id'
        day         column: 'day'
        dayNumber   column: 'day_number'
        
        sort        'dayNumber'
        
        sessionDateTimes cascade: 'all-delete-orphan'
    }

    @Override
    String toString() {
        String dateFormat = messageSource.getMessage('default.date.format', null, LocaleContextHolder.locale)
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat)
        "${dayNumber}: ${sdf.format(day)}"
    }
}
