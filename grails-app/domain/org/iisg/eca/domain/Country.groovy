package org.iisg.eca.domain

/**
 * Domain class of table holding all countries
 */
class Country {
    String tld
    String nameEnglish
    String nameDutch
    String remarks

    static hasMany = [users: User]

    static mapping = {
        table 'countries'
        cache true
        version false

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
        nameEnglish
    }
}
