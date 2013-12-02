package org.iisg.eca.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import grails.plugin.springsecurity.userdetails.GormUserDetailsService

/**
 * Extension of the <code>GormUserDetailsService</code> class to make use of the extended <code>UserDetails</code> class
 */
class UserDetailsService extends GormUserDetailsService {
    @Override
    protected UserDetails createUserDetails(user, Collection<GrantedAuthority> authorities) {
        new MyUserDetails(
                user.email, user.password, user.enabled, !user.deleted,
                authorities, user.id, "${user.firstName} ${user.lastName}", user.salt)
    }
}