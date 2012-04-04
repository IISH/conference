package org.iisg.eca

import java.security.SecureRandom
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Domain class of table holding all registered users
 */
class User extends DefaultDomain {
    /**
	 * The saltSource is responsible for the creation of salts
	 */
    def saltSource

    /**
	 * Information about the currently logged in user
     */
	transient springSecurityService

	String email
	String fullName
	String institute
	Country country
	String language
	String password
	String salt

    static hasMany = [groups: Group]
    static belongsTo = Country

    static mapping = {
		table 'users'
 		version false

        id          column: 'user_id'
        email       column: 'email'
        fullName    column: 'full_name'
        institute   column: 'institute'
        country     column: 'country_id'
        language    column: 'language'
        password    column: 'password'
        salt        column: 'salt'

        groups  joinTable: 'users_groups'
	}

	static constraints = {
		email       maxSize: 30,    blank: false,   unique: true,   email: true
		fullName    maxSize: 30,    blank: false
		institute   maxSize: 50,    blank: false
		country                     blank: false
		language    maxSize: 10,    blank: false
		password    maxSize: 128,   blank: false,   display: false
		salt        maxSize: 26,    nullable: true, display: false
    }

    /**
     * Returns all roles assigned to this user
     * @return A set of roles assigned to this user
     */
    Set<Role> getRoles() {
        UserRole.findAllByUser(this).collect { it.role } as Set
    }

    /**
     * Returns all event dates this user can access
     * @return A set of event dates this user can access
     */
    Set<EventDate> getDates() {
        def roles = Role.findAllByFullRights(true)

        // If the user is granted access to all events, just return a list of all event dates
        // Otherwise, only return the event dates he/she is specifically given access to
        if (SpringSecurityUtils.ifAnyGranted(roles*.role.join(','))) {
            EventDate.list()
        }
        else {
            UserRole.findAllByUser(this).collect { it.date } as Set
        }
    }

    def beforeInsert() {
        // Before insertion of a user, hash the password
        encodePassword()
    }

    def beforeUpdate() {
        // Make sure to hash the password if changed
        if (isDirty('password')) {
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
        password = springSecurityService.encodePassword(password, saltSource.getSalt(this))
    }

    @Override
    def String toString() {
        fullName
    }
}
