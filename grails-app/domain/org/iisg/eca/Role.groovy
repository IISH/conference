package org.iisg.eca

/**
 * Domain class of table holding all available user roles
 */
class Role {
  	String role
	boolean fullRights = false

	static mapping = {
        table 'cms_type_of_user_roles'
        version false
        cache true

        role        column: 'role'
        fullRights  column: 'fullRights'
	}

	static constraints = {
		role    maxSize: 20, blank: false
	}

    @Override
    def String toString() {
        role
    }
}
