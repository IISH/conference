package org.iisg.eca

/**
 * Domain class of table holding all rooms
 */
class Room extends DefaultDomain {
    EventDate date
    String roomName
    String roomNumber
    int noOfSeats
    String comment

    static belongsTo = EventDate
    static hasMany = [roomSessionDateTimeEquipment: RoomSessionDateTimeEquipment]

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
        noOfSeats   column: 'number_of_seets'
        comment     column: 'comment',          type: 'text'
    }

    @Override
    String toString() {
        "${roomName}, ${roomNumber}"
    }
}
