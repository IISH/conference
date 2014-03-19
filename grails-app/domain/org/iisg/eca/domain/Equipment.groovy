package org.iisg.eca.domain

/**
 * Domain class of table holding all available equipment
 */
class Equipment extends EventDateDomain {
    String code
    String equipment
    String description
    String imageUrl
	boolean deleted = false

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
        sort code: 'asc'

        id          column: 'equipment_id'
        code        column: 'code'
        equipment   column: 'equipment'
        description column: 'description',  type: 'text'
        imageUrl    column: 'image_url'
	    deleted     column: 'deleted'

        papers      joinTable: 'paper_equipment'
        
        roomSessionDateTimeEquipment cascade: 'all-delete-orphan'
    }

	static hibernateFilters = {
		dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
		hideDeleted(condition: 'deleted = 0', default: true)
	}

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'code',
            'equipment',
            'description',
            'imageUrl'
    ]

	void softDelete() {
		deleted = true
	}
    
    @Override
    String toString() {
        equipment
    }
}
