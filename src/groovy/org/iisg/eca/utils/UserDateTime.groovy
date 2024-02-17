package org.iisg.eca.utils

import groovy.transform.CompileStatic
import org.iisg.eca.domain.User
import org.iisg.eca.domain.SessionDateTime

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

@CompileStatic
class UserDateTime  {
    private User user
    private SessionDateTime dateTime
    
    UserDateTime(User user, SessionDateTime dateTime) {
        this.user = user
        this.dateTime = dateTime
    }
    
    User getUser() {
        user
    }
    
    SessionDateTime getDateTime() {
        dateTime
    }
    
    @Override
    int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder()
        builder.append user
        builder.append dateTime
        builder.toHashCode()
    }
    
    @Override
    boolean equals(other) {
        if (other == null || !(other instanceof UserDateTime)) {
            return false
        }

        UserDateTime otherUserDateTime = (UserDateTime) other

        EqualsBuilder builder = new EqualsBuilder()
        builder.append user, otherUserDateTime.user
        builder.append dateTime, otherUserDateTime.dateTime
        builder.isEquals()
    }
}
