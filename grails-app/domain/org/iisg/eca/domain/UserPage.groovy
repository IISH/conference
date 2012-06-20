package org.iisg.eca.domain

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib

/**
 * Domain class of table holding all extra rules concerning access to pages
 */
class UserPage extends EventDateDomain implements Serializable {
    private static final ValidationTagLib MESSAGES = new ValidationTagLib()

    User user
    Page page
    boolean denied

    static belongsTo = [User, Page]

    static mapping = {
        table 'users_pages'
        id composite: ['user', 'page']
        cache true
        version false

        user        column: 'user_id'
        page        column: 'page_id'
        date        column: 'date_id'
        denied      column: 'denied'
    }

    static constraints = {
        date    nullable: true
    }

    Long getId() {
        "${user.id}${page.id}".toLong()
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

    @Override
    String toString() {
        String code = (denied) ? "default.access.denied" : "default.access.not.denied"
        "${page} (${MESSAGES.message(code: code)})"
    }
}
