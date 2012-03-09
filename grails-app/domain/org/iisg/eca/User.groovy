package org.iisg.eca

import java.security.SecureRandom

/**
 * Domain class of table holding all registered users
 */
class User {
    def saltSource
	transient springSecurityService

  	String email
	String fullName
	String institute
	String country
	String language
	String encryptedPassword
	String salt
    boolean enabled = true
	boolean deleted = false

	static mapping = {
		table 'cms_users'
 		version false

        email               column: 'email'
        fullName            column: 'fullname'
        institute           column: 'institute'
        country             column: 'country'
        language            column: 'language'
        encryptedPassword   column: 'encryptedpassword'
        salt                column: 'salt'
	}

	static constraints = {
		email               maxSize: 30,    unique: true,   email: true
		fullName            maxSize: 30,    blank: false
		institute           maxSize: 50,    blank: false
		country             maxSize: 5,     blank: false
		language            maxSize: 10,    blank: false
		encryptedPassword   maxSize: 128,   blank: false,   password: true
		salt                maxSize: 26,    nullable: true
        enabled             display: false
        deleted             display: false
    }

    /**
     * Returns all roles assigned to this user
     * @return A set of roles assigned to this user
     */
    Set<Role> getRoles() {
        UserRoles.findAllByUser(this).collect { it.role } as Set
    }

    /**
     * Returns all conferences this user has access to
     * @return A set of all conferences this user has access to
     */
    Set<Conference> getConferences() {
        UserRoles.findAllByUser(this).collect { it.conference } as Set
    }

    /**
     * Returns all conferences this user has access to, restricted to a particular role
     * @param role A role assigned to this user
     * @return A set of all conferences, restricted to the specified role of the user
     */
    Set<Conference> getConferencesByRole(Role role) {
        UserRoles.findAllByUserAndRole(this, role).collect { it.conference } as Set
    }

    def enable(boolean enabled) {
        this.enabled = enabled
    }

    def beforeInsert() {
        // Before insertion of a user, hash the password
        encodePassword()
    }

    def beforeUpdate() {
        // Make sure to hash the password if changed
        if (isDirty('encryptedPassword')) {
            encodePassword()
        }
    }

    def static createSecureRandomString() {
        // Create a 26 characters long String using a secure random generator
        Random r = new SecureRandom()
        new BigInteger(130, r).toString(32)
    }

    protected void encodePassword() {
        // Every time a new password is saved (and has to be hashed), also create a new user salt
        salt = createSecureRandomString()
		encryptedPassword = springSecurityService.encodePassword(encryptedPassword, saltSource.getSalt(this))
	}

    @Override
    def delete() {
        deleted = true
    }

    @Override
    def delete(Map props) {
        deleted = true
    }

    @Override
    def String toString() {
        fullName
    }
}
