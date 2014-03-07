package org.iisg.eca.domain

/**
 * Domain class of table holding all rooms
 */
class Room extends EventDateDomain {
    String roomName
    String roomNumber
    Integer noOfSeats
    String comment
    
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
        
        roomSessionDateTimeEquipment    cascade: 'all-delete-orphan'
        sessionRoomDateTime             cascade: 'all-delete-orphan'
    }

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'roomName',
            'roomNumber',
            'noOfSeats',
            'comment'
    ]

    @Override
    String toString() {
        "${roomName}, ${roomNumber}"
    }
}
