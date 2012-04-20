package org.iisg.eca.domain

class SessionDateTime extends DefaultDomain {
  //  def pageInformation

    Day day
    int index
    String period

    static belongsTo = Day
    static hasMany = [roomSessionDateTimeEquipment: RoomSessionDateTimeEquipment]

    static mapping = {
        table 'session_datetime'
        version false

        id      column: 'session_datetime_id'
        day     column: 'day_id'
        index   column: 'index_number'
        period  column: 'period'
    }

    static constraints = {
        index   min: 0
        period  blank: false,   maxSize: 30
    }

    static hibernateFilters = {
        eventDayFilter(condition: "day_id IN (:days) or day_id = null", types: 'long')
    }

    /*def beforeLoad() {
        enableHibernateFilter('eventDayFilter').setParameter('days', pageInformation.date.days.collect { it.id } as Long[])
    }  */

    @Override
    String toString() {
        "${index} (${period})"
    }
}
