import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.DynamicPage

import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.BaseClientDetails
import org.springframework.security.oauth2.provider.NoSuchClientException

import grails.converters.JSON
import java.text.SimpleDateFormat
import org.springframework.security.core.authority.SimpleGrantedAuthority

class BootStrap {
    def grailsApplication
    def clientDetailsService

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yy-MM-dd")

    def init = { servletContext ->
        // Set the last updated date in the database
        Setting lastUpdated = Setting.findByProperty(Setting.LAST_UPDATED)
        if (!lastUpdated) {
            lastUpdated = new Setting(property: Setting.LAST_UPDATED, value: DATE_FORMAT.format(new Date()))
        }
        else {
            lastUpdated.value = DATE_FORMAT.format(new Date())
        }
        lastUpdated.save()
        
        // Make sure the cache of the dynamic pages are emptied
        DynamicPage.list().each {
            it.cache = null
            it.save()
        }

	    // Make sure we always have a user OAuth 2 client
	    try {
		    clientDetailsService.loadClientByClientId('userClient')
	    }
	    catch (NoSuchClientException nsce) {
		    ClientDetails userClient = new BaseClientDetails()
		    userClient.clientId = 'userClient'
		    userClient.authorizedGrantTypes = ["client_credentials"]
		    userClient.setAuthorities([new SimpleGrantedAuthority('ROLE_USER_API')])

		    clientDetailsService.addClientDetails(userClient)
	    }

        // Make sure domain classes are correctly rendered as JSON
        grailsApplication.domainClasses.each { domainClass ->
            JSON.registerObjectMarshaller(domainClass.clazz) { record ->
                // A record holds a property 'apiAllowed' which states which properties may appear in the JSON output
                if (domainClass.hasProperty('apiAllowed') ) {
                    return record.apiAllowed.collectEntries { property ->
                        // A property is actually a list of nested properties split by a '.'
                        [(property) : property.tokenize('.').inject(record, {obj, prop -> (obj == null) ?: obj[prop] })]
                    }
                }
                else {
                    return [:]
                }
            }
        }
    }
    
    def destroy = {
        
    }
}
