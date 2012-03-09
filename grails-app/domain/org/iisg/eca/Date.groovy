package org.iisg.eca

/**
 * Domain class of table holding all dates a conference took or will take place
 */
class Date {
	String yearCode
    Date startDate
	Date endDate
	String dateAsText
	String description
	String longDescription
	Conference conference
    boolean enabled = true
	boolean deleted = false

	static belongsTo = Conference

	static mapping = {
        table 'dates'
 		version false

        yearCode        column: 'yearcode'
        startDate       column: 'startdate',        sqlType: 'date'
        endDate         column: 'enddate',          sqlType: 'date'
        dateAsText      column: 'dateastext'
        description     column: 'description'
        longDescription column: 'longdescription',  sqlType: 'text'
        conference      column: 'conferenceid'
	}

	static constraints = {
		yearCode        maxSize: 20,    blank: false
		dateAsText      maxSize: 100,   blank: false
        description     maxSize: 255,   blank: false
		longDescription maxSize: 65535, blank: false
        enabled         display: false
        deleted         display: false
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
        yearCode
    }
}
