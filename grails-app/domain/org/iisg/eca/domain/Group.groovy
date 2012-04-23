package org.iisg.eca.domain

/**
 * Domain class of table holding all existing permission groups
 */
class Group extends EventDateDomain {
    def pageInformation

    String name

    static hasMany = [users: User]
    static belongsTo = User

    static mapping = {
        table 'groups'
        version false

        id      column: 'group_id'
        name    column: 'name'
        date    column: 'date_id'

        users   joinTable: 'users_groups'
    }

    static constraints = {
        name    blank: false,   maxSize: 30
        date    nullable: true
    }
    
    Set<Page> getPages() {
        GroupPage.findAllByGroup(this).collect { it.page } as Set
    }

    @Override
    String toString() {
        name
    }
}
