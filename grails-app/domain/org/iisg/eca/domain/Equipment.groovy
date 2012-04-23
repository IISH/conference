package org.iisg.eca.domain

/**
 * Domain class of table holding all available equipment
 */
class Equipment extends EventDateDomain {
    String equipment
    String description
    String imageUrl
    
    static hasMany = [roomSessionDateTimeEquipment: RoomSessionDateTimeEquipment]

    static constraints = {
        equipment   blank: false,   maxSize: 30
        description nullable: true
        imageUrl    nullable: true, maxSize: 50,    url: true   
    }

    static mapping = {
        table 'equipment'
        version false

        id          column: 'equipment_id'
        equipment   column: 'equipment'
        description column: 'description',  type: 'text'
        imageUrl    column: 'image_url'
    }
    
    @Override
    String toString() {
        equipment
    }
}
