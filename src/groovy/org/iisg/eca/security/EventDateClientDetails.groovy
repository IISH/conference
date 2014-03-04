package org.iisg.eca.security

import org.springframework.security.oauth2.provider.BaseClientDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority

/**
 * Extension of the <code>BaseClientDetails</code> class to include extra event information
 * necessary for authorization of specific APIs
 */
class EventDateClientDetails extends BaseClientDetails {
    EventDateClientDetails() {
        super()
        this.authorizedGrantTypes = ["client_credentials"]
        this.setAuthorities([new SimpleGrantedAuthority('ROLE_API')])
    }

    void setEvents(String events) {
        addAdditionalInformation('events', events)
    }

    String[] getEvents() {
	    String events = getAdditionalInformation().get('events')
        events.split(',')
    }
}
