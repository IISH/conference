package org.iisg.eca

import org.springframework.security.core.GrantedAuthority
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUser

/**
 * Extension of the <code>GrailsUser</code> class to include extra user information necessary
 * for authentication and session information extraction
 */
class MyUserDetails extends GrailsUser {
    // Necessary for displaying the name of the logged in user on every page
    final String fullName
    // Necessary for displaying the messages in the correct language
    final String language
    // Necessary for comparing hashed passwords during authentication
    final String salt

    MyUserDetails(String username, String password, boolean enabled, boolean accountNonLocked,
            Collection<GrantedAuthority> authorities, long id, String fullName, String language, String salt) {
        super(username, password, enabled, true, true, accountNonLocked, authorities, id)
        this.fullName = fullName
        this.language = language
        this.salt = salt
    }
}