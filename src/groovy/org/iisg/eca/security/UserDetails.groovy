package org.iisg.eca.security

import org.springframework.security.core.GrantedAuthority
import grails.plugin.springsecurity.userdetails.GrailsUser

/**
 * Extension of the <code>GrailsUser</code> class to include extra user information necessary
 * for authentication and session information extraction
 */
class MyUserDetails extends GrailsUser {
    // Necessary for displaying the name of the logged in user on every page
    final String fullName
    // Necessary for comparing hashed passwords during authentication
    final String salt

    MyUserDetails(String username, String password, boolean enabled,
                    boolean accountNonLocked,
                    Collection<GrantedAuthority> authorities,
                    long id, String fullName, String salt) {
        super(username, password, enabled, true, true, accountNonLocked, authorities, id)

        this.fullName = fullName
        this.salt = salt
    }
}