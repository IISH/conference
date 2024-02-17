import org.iisg.eca.domain.OAuthClientDetails
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.DynamicPage

import org.springframework.security.oauth2.provider.NoSuchClientException

import grails.converters.JSON
import grails.util.Environment
import java.text.SimpleDateFormat

class BootStrap {
	def grailsApplication
	def gormClientDetailsService

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd")

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
		if (Environment.current != Environment.TEST) {
			try {
				gormClientDetailsService.loadClientByClientId('userClient')
			}
			catch (NoSuchClientException nsce) {
				OAuthClientDetails userClient = new OAuthClientDetails(
						clientId: 'userClient',
						authority: 'ROLE_USER_API',
						grantType: 'client_credentials',
				)
				userClient.save()
			}
		}

		// Make sure domain classes are correctly rendered as JSON
		grailsApplication.domainClasses.each { domainClass ->
			JSON.registerObjectMarshaller(domainClass.clazz) { record ->
				// A record holds a property 'apiAllowed' which states which properties may appear in the JSON output
				if (domainClass.hasProperty('apiAllowed')) {
					return record.apiAllowed.collectEntries { property ->
						// A property is actually a list of nested properties split by a '.'
						[(property): property.tokenize('.').
								inject(record, { obj, prop -> (obj != null) ? obj[prop] : null })]
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
