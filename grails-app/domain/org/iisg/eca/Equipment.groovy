package org.iisg.eca

/**
 * Domain class of table holding all available equipment
 */
class Equipment extends DefaultDomain {
    EventDate date
    String equipment
    String description
    String imageUrl

    static belongsTo = EventDate
    static hasMany = [roomSessionDateTimeEquipment: RoomSessionDateTimeEquipment]

    static constraints = {
        date        nullable: true
        equipment   blank: false,   maxSize: 30
        description nullable: true
        imageUrl    nullable: true, maxSize: 50
    }

    static mapping = {
        table 'equipment'
        version false

        id          column: 'equipment_id'
        date        column: 'date_id'
        equipment   column: 'equipment'
        description column: 'description',  type: 'text'
        imageUrl    column: 'image_url'
    }

    @Override
    String toString() {
        equipment
    }
}
