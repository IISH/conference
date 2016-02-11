package org.iisg.eca.domain

/**
 * Domain class of table holding all days and times during which sessions can be planned
 */
class SessionDateTime extends EventDateDomain implements Comparable<SessionDateTime> {
    Day day
    int indexNumber
    String period
	boolean deleted = false

    static belongsTo = [Day, User]
    static hasMany = [  roomSessionDateTimeEquipment: RoomSessionDateTimeEquipment,
                        sessionRoomDateTime: SessionRoomDateTime,
                        usersNotPresent: User]

    static mapping = {
        table 'session_datetime'
        version false
        sort 'indexNumber'

        id              column: 'session_datetime_id'
        day             column: 'day_id'
        indexNumber     column: 'index_number'
        period          column: 'period'
	    deleted         column: 'deleted'

        usersNotPresent                 joinTable: [name: 'participant_not_present', key: 'session_datetime_id' ]
        roomSessionDateTimeEquipment    cascade: 'all-delete-orphan'
        sessionRoomDateTime             cascade: 'all-delete-orphan'
    }

    static constraints = {
        indexNumber     min: 0
        period          blank: false,   maxSize: 30
    }

	static hibernateFilters = {
		dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
		hideDeleted(condition: 'deleted = 0', default: true)
	}

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'day.id',
            'indexNumber',
            'period'
    ]

	void softDelete() {
		deleted = true
	}

    /**
     * Searches for all date times and tries to order them in a table layout
     * @return A list with SessionDateTime rows
     */
    static List<List<SessionDateTime>> getTableList(EventDate date) {
        List<List<SessionDateTime>> mainList = new ArrayList<List<SessionDateTime>>()
        List<SessionDateTime> curList = new ArrayList<SessionDateTime>()
        int curDayId = -1
        
        SessionDateTime.withCriteria {           
            eq('date.id', date.id)
            order('indexNumber', 'asc')
        }.each { sessionDateTime ->
            if ((curDayId != -1) && (sessionDateTime.day.id != curDayId)) {
                mainList.add(curList)
                curList = new ArrayList<SessionDateTime>()
            }

            curDayId = sessionDateTime.day.id
            curList.add(sessionDateTime)
        }
         
        mainList.add(curList)
        mainList
    }

    @Override
    public int compareTo(SessionDateTime o) {
        indexNumber.compareTo(o.indexNumber)
    }
    
    @Override
    String toString() {
        "${indexNumber} (${period})"
    }
}
