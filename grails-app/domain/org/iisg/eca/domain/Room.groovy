package org.iisg.eca.domain

/**
 * Domain class of table holding all rooms
 */
class Room extends DefaultDomain {
    def pageInformation

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

    static hibernateFilters = {
        eventDateFilter(condition: "date_id = (:dateId) or date_id = null", types: 'long')
    }

    /*def beforeLoad() {
        if (pageInformation.date) {
            enableHibernateFilter('eventDateFilter').setParameter('dateId', pageInformation.date.id)
        }
    }  */

   /* def beforeInsert() {
        if (pageInformation.date) {
            date = pageInformation.date
        }
    }

    def beforeUpdate() {
        if (pageInformation.date) {
            date = pageInformation.date
        }
    } */

    @Override
    String toString() {
        "${roomName}, ${roomNumber}"
    }
}
