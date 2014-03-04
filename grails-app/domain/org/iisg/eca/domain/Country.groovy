package org.iisg.eca.domain

import org.springframework.context.i18n.LocaleContextHolder

/**
 * Domain class of table holding all countries
 */
class Country {
    String tld
    String isoCode
    String nameEnglish
    String nameDutch
    String remarks

    static belongsTo = Country

    static hasMany = [users: User, exemptCountries: Country]

    static mapping = {
        table 'countries'
        cache true
        version false

        id                  column: 'country_id'
        tld                 column: 'tld'
        isoCode             column: 'iso_code'
        nameEnglish         column: 'name_english'
        nameDutch           column: 'name_dutch'
        remarks             column: 'remarks',      type: 'text'

        exemptCountries     joinTable: [name: 'country_exemptions', key: 'country_id', column: 'exempt_country_id']
    }

    static constraints = {
        tld                 blank: false,   maxSize: 2,     unique: true
        isoCode             nullable: true, maxSize: 2
        nameEnglish         blank: false,   maxSize: 50
        nameDutch           blank: false,   maxSize: 50
        remarks             nullable: true
        exemptCountries     nullable: true
    }

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'tld',
            'isoCode',
            'nameEnglish',
            'nameDutch',
		    'exemptCountries.id'
    ]

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
