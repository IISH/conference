package org.iisg.eca

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * Domain class of table holding all roles assigned to a participant
 */
class UserRole implements Serializable {
	User user
	Role role
    EventDate date

    static belongsTo = [User, Role, EventDate]

    static mapping = {
        table 'users_roles'
		version false

        id      column: 'user_role_id'
        user    column: 'user_id'
        role    column: 'role_id'
        date    column: 'date_id'
	}

    static constraints = {
        date    nullable: true
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

    /**
     * Grants a specific user a new role and/or grants a specific user access to a specific event date
     * @param user The user in question
     * @param role The new role to be assigned to the user
     * @param flush If specified and true, flushes the Hibernate session, saving the changes immediately to the database
     * @return The new corresponding UserRole domain class
     */
	static UserRole create(User user, Role role, boolean flush = false) {
	    new UserRole(user: user, role: role).save(flush: flush, insert: true)
	}

    /**
     * Removes a specific role of the given user and the event dates assigned to that role
     * @param user The user in question
     * @param role The role to be taken away from the user
     * @param flush If specified and true, flushes the Hibernate session, saving the changes immediately to the database
     * @return Whether the role and its event dates were removed for this specific user
     */
    static boolean remove(User user, Role role, boolean flush = false) {
		List<UserRole> instances = UserRole.findAllByUserAndRole(user, role)
		if (!instances) {
			return false
		}

		instances.each {
            it.delete(flush: flush)
        }
		return true
	}

    /**
     * Takes away all roles of the given user
     * @param user The user in question
     */
	static void removeAll(User user) {
		executeUpdate 'DELETE FROM UserRole WHERE user=:user', [user: user]
	}

    /**
     * Takes away the given role from all users
     * @param role The role in question
     */
	static void removeAll(Role role) {
		executeUpdate 'DELETE FROM UserRole WHERE role=:role', [role: role]
	}
}
