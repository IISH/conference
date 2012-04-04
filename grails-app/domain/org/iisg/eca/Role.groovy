package org.iisg.eca

/**
 * Domain class of table holding all available user roles
 */
class Role {
	String role
	String description
	boolean fullRights = false

	static mapping = {
        table 'roles'
        version false
        cache true

        id          column: 'role_id'
        role        column: 'role'
        description column: 'description',  sqlType: 'tinyText'
        fullRights  column: 'full_rights'
	}

	static constraints = {
        description maxSize: 200,   blank: false
		role        maxSize: 20,    nullable: true
	}

    Set<User> getUsers() {
        UserRole.findAllByRole(this).collect { it.user } as Set
    }

    @Override
    def String toString() {
        role
    }
}
