package org.iisg.eca.domain

/**
 * Domain class of table holding all rooms
 */
class Room extends EventDateDomain {
    String roomName
    String roomNumber
    Integer noOfSeats
    String comment
	boolean deleted = false
    
    static hasMany = [roomSessionDateTimeEquipment: RoomSessionDateTimeEquipment, sessionRoomDateTime: SessionRoomDateTime]
    
    static constraints = {
        date        nullable: true
        roomName    blank: false,       maxSize: 30
        roomNumber  blank: false,       maxSize: 10
        noOfSeats   nullable: false
        comment     nullable: true
    }

    static mapping = {
        table 'rooms'
        version false

        id          column: 'room_id'
        date        column: 'date_id'
        roomName    column: 'room_name'
        roomNumber  column: 'room_number'
        noOfSeats   column: 'number_of_seats'
        comment     column: 'comment',          type: 'text'
	    deleted     column: 'deleted'
        
        roomSessionDateTimeEquipment    cascade: 'all-delete-orphan'
        sessionRoomDateTime             cascade: 'all-delete-orphan'
    }

	static hibernateFilters = {
		dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
		hideDeleted(condition: 'deleted = 0', default: true)
	}

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'roomName',
            'roomNumber',
            'noOfSeats',
            'comment'
    ]

	void softDelete() {
		deleted = true
	}

    @Override
    String toString() {
        "${roomName}, ${roomNumber}"
    }
}
