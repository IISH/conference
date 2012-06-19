package org.iisg.eca.domain

/**
 * Domain class of table holding all existing permission groups
 */
class Group extends EventDateDomain {
    String name

    static hasMany = [users: User, pages: Page]

    static mapping = {
        table 'groups'
        version false

        id      column: 'group_id'
        name    column: 'name'
        date    column: 'date_id'

        users   joinTable: 'users_groups'
        pages   joinTable: 'groups_pages'
    }

    static constraints = {
        name    blank: false,   maxSize: 30
        date    nullable: true
    }

    @Override
    String toString() {
        name
    }
}
