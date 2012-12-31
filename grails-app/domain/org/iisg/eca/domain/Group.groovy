package org.iisg.eca.domain

/**
 * Domain class of table holding all existing permission groups
 */
class Group extends EventDateDomain {
    String name

    static hasMany = [users: User, pages: Page]

    static mapping = {
        table 'groups'
        cache true
        version false
        sort name: 'asc'

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
    
    static Set<Group> getAllGroupsOfUser(User user) {
        Group.withCriteria { 
            users { 
                eq('id', user.id) 
            } 
        }
    }
    
    static Set<Group> getAllGroupsWithAccess() {
        Set<Group> groups = []
        Group.list().each { 
            if (it.pages.findAll { p -> !p.hasAccess() }.size() == 0) {
                groups.add(it) 
            }
        }
        groups
    }

    @Override
    String toString() {
        name
    }
}
