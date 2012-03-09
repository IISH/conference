package org.iisg.eca

/**
 * Domain class of table holding all conferences
 */
class Conference {
	String shortName
	String longName
	String type
    boolean enabled = true
	boolean deleted = false

	static hasMany = [dates: Date]

	static mapping = {
		table 'conference'
 		version false

        shortName   column: 'shortname'
        longName    column: 'longname'
        type        column: 'type'
	}

	static constraints = {
		shortName   maxSize: 20,    blank: false
		longName    maxSize: 50,    blank: false
		type        maxSize: 20,    blank: false
        enabled     display: false
        deleted     display: false
	}

    def enable(boolean enabled) {
        this.enabled = enabled
    }

    @Override
    def delete() {
        deleted = true
    }

    @Override
    def delete(Map props) {
        deleted = true
    }

    @Override
    def String toString() {
        shortName
    }
}
