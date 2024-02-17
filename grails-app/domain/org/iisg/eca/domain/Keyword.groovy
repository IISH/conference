package org.iisg.eca.domain

/**
 * Domain class of table holding all possible keywords
 */
class Keyword extends EventDomain {
    String groupName
    String keyword

    static mapping = {
        table 'keywords'
	    cache true
        version false

        id          column: 'keyword_id'
        groupName   column: 'group_name'
        keyword     column: 'keyword'
    }

    static constraints = {
        keyword     blank: false,   maxSize: 255
        groupName   blank: false,   maxSize: 255
    }

    static apiActions = ['GET']

    static apiAllowed = [
        'id',
        'groupName',
        'keyword'
    ]

    static Set<String> getGroups() {
        return new HashSet<>(executeQuery("SELECT groupName FROM Keyword GROUP BY groupName"))
    }

    @Override
    String toString() {
        return groupName + ': ' + keyword
    }
}
