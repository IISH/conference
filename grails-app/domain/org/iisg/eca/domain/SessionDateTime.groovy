package org.iisg.eca.domain

/**
 * Domain class of table holding all days and times during which sessions can be planned
 */
class SessionDateTime extends DefaultDomain {
    Day day
    int index
    String period

    static belongsTo = [Day, User]
    static hasMany = [  roomSessionDateTimeEquipment: RoomSessionDateTimeEquipment,
                        sessionRoomDateTime: SessionRoomDateTime,
                        usersNotPresent: User]

    static mapping = {
        table 'session_datetime'
        version false
        sort 'index'

        id      column: 'session_datetime_id'
        day     column: 'day_id'
        index   column: 'index_number'
        period  column: 'period'

        usersNotPresent joinTable: [name: 'participant_not_present', key: 'session_datetime_id' ]
    }

    static constraints = {
        index   min: 0
        period  blank: false,   maxSize: 30
    }

    /**
     * Searches for all date times and tries to order them in a table layout
     * @return A list with SessionDateTime rows
     */
    static List<List<SessionDateTime>> getTableList() {
        List<List<SessionDateTime>> mainList = new ArrayList<List<SessionDateTime>>()
        List<SessionDateTime> curList = new ArrayList<SessionDateTime>()
        int curDayId = -1

        SessionDateTime.listOrderByIndex().each { sessionDateTime ->
            if ((curDayId != -1) && (sessionDateTime.day.id != curDayId)) {
                mainList.add(curList)
                curList =  new ArrayList<SessionDateTime>()
            }

            curDayId = sessionDateTime.day.id
            curList.add(sessionDateTime)
        }

        mainList.add(curList)
        mainList
    }
    
    @Override
    String toString() {
        "${index} (${period})"
    }
}
