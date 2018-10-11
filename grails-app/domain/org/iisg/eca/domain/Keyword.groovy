package org.iisg.eca.domain

/**
 * Domain class of table holding all possible keywords
 */
class Keyword extends EventDomain {
    String keyword

    static mapping = {
        table 'keywords'
	    cache true
        version false

        id          column: 'keyword_id'
        keyword     column: 'keyword'
    }

    static constraints = {
        keyword     blank: false,   maxSize: 255
    }

    static apiActions = ['GET']

    static apiAllowed = [
        'id',
        'keyword'
    ]

    @Override
    String toString() {
        return keyword
    }
}
