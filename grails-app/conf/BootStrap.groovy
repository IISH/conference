import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.iisg.eca.domain.OAuthClientDetails
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.DynamicPage
import org.iisg.eca.filter.SoftDelete
import org.springframework.security.oauth2.provider.NoSuchClientException

import grails.converters.JSON
import grails.util.Environment
import java.text.SimpleDateFormat

class BootStrap {
	def grailsApplication
	def gormClientDetailsService

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd")

	def init = { servletContext ->
		updateLastUpdated()
		emptyDynamicPageCache()
		createOAuth2UserClient()

		for (GrailsClass grailsClass : grailsApplication.domainClasses) {
			GrailsDomainClass domainClass = (GrailsDomainClass) grailsClass

			registerJSONForDomainClass(domainClass)
			implementSoftDeleteForDomainClass(domainClass)
		}
	}

	def destroy = {

	}

	private void updateLastUpdated() {
		Setting lastUpdated = Setting.findByProperty(Setting.LAST_UPDATED)
		if (!lastUpdated) {
			lastUpdated = new Setting(property: Setting.LAST_UPDATED, value: DATE_FORMAT.format(new Date()))
		}
		else {
			lastUpdated.value = DATE_FORMAT.format(new Date())
		}
		lastUpdated.save()
	}

	private void emptyDynamicPageCache() {
		DynamicPage.list().each {
			it.cache = null
			it.save()
		}
	}

	private void createOAuth2UserClient() {
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
	}

	private void registerJSONForDomainClass(GrailsDomainClass domainClass) {
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

	private void implementSoftDeleteForDomainClass(GrailsDomainClass domainClass) {
		Class domain = domainClass.clazz
		if (domain.isAnnotationPresent(SoftDelete)) {
			domain.metaClass.softDelete = {
				SoftDelete softDelete = this.class.getAnnotation(SoftDelete)
				this."${softDelete.value()}" = true
			}
		}
	}
}
