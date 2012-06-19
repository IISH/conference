package org.iisg.eca.domain

/**
 * Domain class of table holding all available equipment
 */
class Equipment extends EventDateDomain {
    String code
    String equipment
    String description
    String imageUrl

    static belongsTo = Paper
    static hasMany = [roomSessionDateTimeEquipment: RoomSessionDateTimeEquipment, papers: Paper]

    static constraints = {
        code        blank: false,   maxSize: 1
        equipment   blank: false,   maxSize: 30
        description nullable: true
        imageUrl    nullable: true, maxSize: 50,    url: true   
    }

    static mapping = {
        table 'equipment'
        version false

        id          column: 'equipment_id'
        code        column: 'code'
        equipment   column: 'equipment'
        description column: 'description',  type: 'text'
        imageUrl    column: 'image_url'

        papers      joinTable: 'paper_equipment'
        
        roomSessionDateTimeEquipment cascade: 'all-delete-orphan'
    }
    
    @Override
    String toString() {
        equipment
    }
}
