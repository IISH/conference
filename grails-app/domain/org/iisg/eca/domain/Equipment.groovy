package org.iisg.eca.domain

/**
 * Domain class of table holding all available equipment
 */
class Equipment extends DefaultDomain {
   // def pageInformation

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

    static hibernateFilters = {
        eventDateFilter(condition: "date_id = (:dateId) or date_id = null", types: 'long')
    }

   /* def beforeLoad() {
        enableHibernateFilter('eventDateFilter').setParameter('dateId', pageInformation.date.id)
    }  */

    /*def beforeInsert() {
        date = pageInformation.date
    }

    def beforeUpdate() {
        date = pageInformation.date
    }   */

    @Override
    String toString() {
        equipment
    }
}
