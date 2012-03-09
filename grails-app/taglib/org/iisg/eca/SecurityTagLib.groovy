package org.iisg.eca

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Tag lib involving security data
 */
class SecurityTagLib {
    static namespace = "eca"

    /**
     * Tag printing all roles of the logged in user in a user-friendly way
     */
    def roles = {
        String userRoles = SpringSecurityUtils.getPrincipalAuthorities().collect { it.authority }.join(', ')
        if (!userRoles.isEmpty()) {
            out << "(${userRoles})"
        }
    }
}
