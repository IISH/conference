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
    boolean deleted
    
    static hasMany = [sessionDateTimes: SessionDateTime, participantPresent: ParticipantDay]
    
    static constraints = {
        dayNumber   min: 0, validator: { val, obj ->
                        if (obj.date?.startDate && obj.date?.endDate && ((obj.day < obj.date.startDate) || (obj.day > obj.date.endDate))) {                            
                            ["day.validation.between.message", obj.getFormat().format(obj.date?.startDate), obj.getFormat().format(obj.date?.endDate)]
                        }
                    }
    }

    static mapping = {
        table 'days'
        version false

        id          column: 'day_id'
        day         column: 'day'
        dayNumber   column: 'day_number'
        deleted     column: 'deleted'
        
        sort        'dayNumber'
        
        sessionDateTimes cascade: 'all-delete-orphan'
    }

    static hibernateFilters = {
        dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
        hideDeleted(condition: 'deleted = 0', default: true)
    }

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'day',
            'dayNumber'
    ]

    void softDelete() {
        deleted = true
    }
    
    SimpleDateFormat getFormat() {
        String dateFormat = messageSource.getMessage('default.date.format', null, LocaleContextHolder.locale)
        new SimpleDateFormat(dateFormat, LocaleContextHolder.locale)      
    }

    @Override
    String toString() {        
        "${dayNumber}: ${getFormat().format(day)}"
    }
}
