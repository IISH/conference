package org.iisg.eca

/**
 * Domain class of table holding all settings
 */
class Setting {
	String property
	String value
    Event event

    static belongsTo = Event

	static mapping = {
        table 'settings'
		version false

        id          column: 'setting_id'
        property    column: 'property'
        value       column: 'value'
        event       column: 'event_id'
	}

	static constraints = {
		property    blank: false,   maxSize: 50
        value       blank: false,   maxSize: 255
		event       nullable: true
	}
}
