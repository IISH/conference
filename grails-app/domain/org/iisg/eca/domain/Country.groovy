package org.iisg.eca.domain

import org.springframework.context.i18n.LocaleContextHolder

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
        // Find out the currently selected language by the user and return the country name in that language
        switch (LocaleContextHolder.locale.language) {
            case 'nl':
                nameDutch
                break
            case 'en':
            default:
                nameEnglish
        }
    }
}
