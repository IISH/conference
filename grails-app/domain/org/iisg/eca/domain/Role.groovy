package org.iisg.eca.domain

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
        cache true

        id          column: 'role_id'
        role        column: 'role'
        description column: 'description',  type: 'text'
        fullRights  column: 'full_rights'
        
        userRoles   cascade: 'all-delete-orphan'
    }

    static constraints = {
        role        blank: false
        description nullable: true
    }

    @Override
    String toString() {
        role
    }
}
