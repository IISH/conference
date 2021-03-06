package org.iisg.eca.domain

/**
 * Domain class of table holding all existing permission groups
 */
class Group extends EventDomain {
    String name
	boolean deleted = false

    static hasMany = [users: User, pages: Page]

    static mapping = {
        table 'groups'
        cache true
        version false
        sort name: 'asc'

        id      column: 'group_id'
        name    column: 'name'
        deleted column: 'deleted'

        users   joinTable: 'users_groups'
        pages   joinTable: 'groups_pages'
    }

    static constraints = {
        name    blank: false,   maxSize: 50
    }

	static hibernateFilters = {
		eventFilter(condition: '(event_id = :eventId OR event_id IS NULL)', types: 'long')
		hideDeleted(condition: 'deleted = 0', default: true)
	}

	void softDelete() {
		deleted = true
	}

    List<Page> getAllPagesInGroup() {
        Page.withCriteria {
            groups {
                eq('id', id)
            }
            order('titleDefault', "asc")
        }
    }

    List<Page> getAllPagesNotInGroup() {
        List<Page> pagesNotInGroup = Page.getAllPagesWithAccess()
        List<Long> allPageIds = getAllPagesInGroup()*.id
        pagesNotInGroup.removeAll { allPageIds.contains(it.id) }

        pagesNotInGroup
    }

    List<User> getAllUsersInGroup() {
        User.withCriteria {
            groups {
                eq('id', id)
            }
            order('lastName', "asc")
            order('firstName', "asc")
        }
    }
    
    static List<Group> getAllGroupsOfUser(User user) {
        Group.withCriteria { 
            users { 
                eq('id', user.id) 
            }
            order('name', 'asc')
        }
    }
    
    static List<Group> getAllGroupsWithAccess() {
        List<Group> groups = []
        Group.listOrderByName().each {
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
