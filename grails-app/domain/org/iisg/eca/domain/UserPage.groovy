package org.iisg.eca.domain

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * Domain class of table holding all extra rules concerning access to pages
 */
class UserPage extends EventDateDomain implements Serializable {
    User user
    Page page
    boolean denied
    boolean showInMenu

    static belongsTo = [User, Page]

    static mapping = {
        table 'users_pages'
        id composite: ['user', 'page']
        version false

        user        column: 'user_id'
        page        column: 'page_id'
        date        column: 'date_id'
        denied      column: 'denied'
        showInMenu  column: 'show_in_menu'
    }

    static constraints = {
        date    nullable: true
    }
    
    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append user
        builder.append page
        builder.toHashCode()
    }

    boolean equals(other) {
        if (other == null || !(other instanceof UserPage)) {
            return false
        }

        def builder = new EqualsBuilder()
        builder.append user, other.user
        builder.append page, other.page
        builder.isEquals()
    }
}
