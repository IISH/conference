package org.iisg.eca.domain

/**
 * Domain class of table holding all days and times during which sessions can be planned
 */
class SessionDateTime extends DefaultDomain {
    Day day
    int index
    String period

    static belongsTo = Day
    static hasMany = [roomSessionDateTimeEquipment: RoomSessionDateTimeEquipment, sessionRoomDateTime: SessionRoomDateTime]

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
    
    @Override
    String toString() {
        "${index} (${period})"
    }
}
