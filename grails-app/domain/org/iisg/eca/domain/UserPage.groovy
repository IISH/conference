package org.iisg.eca.domain

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.springframework.context.i18n.LocaleContextHolder

/**
 * Domain class of table holding all extra rules concerning access to pages
 */
class UserPage extends EventDomain implements Serializable {
    def messageSource

    User user
    Page page
    boolean denied

    static belongsTo = [User, Page]

    static mapping = {
        table 'users_pages'
        id composite: ['user', 'page']
        version false

        user        column: 'user_id'
        page        column: 'page_id'
        denied      column: 'denied'
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
        "${page} (${messageSource.getMessage(code, null, LocaleContextHolder.locale)})"
    }
}
