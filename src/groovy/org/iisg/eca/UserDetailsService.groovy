package org.iisg.eca

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import org.codehaus.groovy.grails.plugins.springsecurity.GormUserDetailsService

/**
 * Extension of the <code>GormUserDetailsService</code> class to make use of the extended <code>UserDetails</code> class
 */
class UserDetailsService extends GormUserDetailsService {
    @Override
    protected UserDetails createUserDetails(user, Collection<GrantedAuthority> authorities) {
        new MyUserDetails(user.email, user.password, user.enabled, !user.deleted, authorities, user.id,
                user.fullName, user.language, user.salt)
    }
}