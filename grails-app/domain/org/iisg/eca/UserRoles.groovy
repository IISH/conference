package org.iisg.eca

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * Domain class representing the m:n join table between the User and Roles tables/domain classes
 */
class UserRoles implements Serializable {
  	User user
	Role role
    Conference conference

	static mapping = {
		table 'cms_users_roles'
		version false
        id composite: ['role', 'user']

        user column: 'user_id'
        role column: 'role_id'
        conference column: 'conference_id'
	}

	static constraints = {
		conference nullable: true
	}

    /**
     * Grants a specific user a new role and/or grants a specific user access to a specific conference
     * @param user The user in question
     * @param role The new role to be assigned to the user and/or the role for which to add conference access
     * @param conference If specified, gives the user access to this conference
     * @param flush If specified and true, flushes the Hibernate session, saving the changes immediately to the database
     * @return The new corresponding UserRoles domain class
     */
	static UserRoles create(User user, Role role, Conference conference = null, boolean flush = false) {
	    new UserRoles(user: user, role: role, conference: conference).save(flush: flush, insert: true)
	}

    /**
     * Removes a specific role of the given user and the conferences assigned to that role
     * @param user The user in question
     * @param role The role to be taken away from the user
     * @param flush If specified and true, flushes the Hibernate session, saving the changes immediately to the database
     * @return Whether the role and its conferences were removed for this specific user
     */
    static boolean remove(User user, Role role, boolean flush = false) {
		List<UserRoles> instances = UserRoles.findAllByUserAndRole(user, role)
		if (!instances) {
			return false
		}

		instances.each {
            it.delete(flush: flush)
        }
		return true
	}

	static void removeAll(User user) {
		executeUpdate 'DELETE FROM UserRoles WHERE user=:user', [user: user]
	}

	static void removeAll(Role role) {
		executeUpdate 'DELETE FROM UserRoles WHERE role=:role', [role: role]
	}

    @Override
	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append user
		builder.append role
		builder.toHashCode()
	}

    @Override
	boolean equals(other) {
		if (other == null) {
            return false
        }

		def builder = new EqualsBuilder()
		builder.append user, other.user
		builder.append role, other.role
		builder.isEquals()
	}
}
