package org.iisg.eca

/**
 * Domain class of table holding all countries
 */
class Country extends DefaultDomain {
	String tld
	String nameEnglish
	String nameDutch
	String remarks

    static hasMany = [users: User]

	static mapping = {
		table 'countries'
 		version false
        cache true

        id          column: 'country_id'
        tld         column: 'tld'
        nameEnglish column: 'name_english'
        nameDutch   column: 'name_dutch'
        remarks     column: 'remarks'
	}

	static constraints = {
		tld         maxSize: 2,     blank: false,   unique: true
		nameEnglish maxSize: 50,    blank: false
		nameDutch   maxSize: 50,    blank: false
        remarks     maxSize: 200,   nullable: true
	}

    @Override
    def String toString() {
        "${tld}, ${nameEnglish}"
    }
}
