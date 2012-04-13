package org.iisg.eca

/**
 * Domain class of table holding all existing permission groups
 */
class Group extends DefaultDomain {
	String name
    EventDate date

	static hasMany = [users: User]
    static belongsTo = [EventDate, User]

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
