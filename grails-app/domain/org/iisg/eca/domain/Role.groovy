package org.iisg.eca.domain

import grails.plugin.springsecurity.SpringSecurityUtils

/**
 * Domain class of table holding all available user roles
 */
class Role {
    String role
    String description
    boolean fullRights = false

    static hasMany = [userRoles: UserRole]

    static mapping = {
        table 'roles'
        version false

        id          column: 'role_id'
        role        column: 'role'
        description column: 'description',  type: 'text'
        fullRights  column: 'full_rights'
    }

    static constraints = {
        role        blank: false
        description nullable: true
    }
    
    static Set<Role> getPossibleRoles() {
        Set<Role> roles = []
        Role.list().each { 
            if (SpringSecurityUtils.ifAnyGranted(it.role)) {
                roles.add(it) 
            }
        }
        roles
    }

    @Override
    String toString() {
        role
    }
}
