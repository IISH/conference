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
        remarks     column: 'remarks',      type: 'text'
	}

	static constraints = {
		tld         blank: false,   maxSize: 2,     unique: true
		nameEnglish blank: false,   maxSize: 50
		nameDutch   blank: false,   maxSize: 50
        remarks     nullable: true
	}

    @Override
    String toString() {
        "${tld}, ${nameEnglish}"
    }
}