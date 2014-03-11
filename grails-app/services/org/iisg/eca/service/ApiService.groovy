package org.iisg.eca.service

import org.iisg.eca.utils.DomainClassInfo
import org.iisg.eca.utils.PageInformation

import grails.orm.PagedResultList
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * The API service for automatic CRUD actions through the API interface
 */
class ApiService {
	private static final int MAX_NUM_RETURNED_RECORDS = 1000

	PageInformation pageInformation
	GrailsApplication grailsApplication

	/**
	 * Returns a list of results for the given domain class and the given criteria
	 * @param domain The name of the domain class
	 * @param params The criteria to query on
	 * @return The results
	 * @throws Exception
	 */
	PagedResultList getWithCriteria(String domain, Map params) throws Exception {
		DomainClassInfo domainClassInfo = new DomainClassInfo(grailsApplication, domain)
		List<String> allowed = getAllowed(domainClassInfo)
		Map validProperties = params.subMap(params.keySet().intersect(allowed)).findAll { it.value instanceof String }

		int max = (params.max?.isInteger() && params.max <= MAX_NUM_RETURNED_RECORDS) ? params.int('max') :
				MAX_NUM_RETURNED_RECORDS
		int offset = (params.offset?.isInteger()) ? params.int('offset') : 0
		String sort = allowed.contains(params.sort) ? params.sort : null
		String order = params.order.equals('desc') ? 'desc' : 'asc'

		Map<String, Object> props = [max: max, offset: offset]
		if (sort) {
			props.put('sort', sort)
			props.put('order', order)
		}

		return domainClassInfo.getDomainClass().createCriteria().list(props) {
			validProperties.each { property, value ->
				def (val, method) = value.toString().split('::')

				if (method == null) {
					method = 'eq'
				}

				DomainClassInfo infoForProperty = domainClassInfo
				def alias = null
				def prop = property
				if (property.toString().contains('.')) {
					(alias, prop) = property.toString().split('\\.')
					createAlias(alias, alias)
					infoForProperty = domainClassInfo.getDomainClassInfoForProperty(alias)
					if (infoForProperty.hasProperty('event')) {
						eq(alias + '.event.id', pageInformation.date.event.id)
					}
					if (infoForProperty.hasProperty('date')) {
						eq(alias + '.date.id', pageInformation.date.id)
					}
					if (infoForProperty.hasProperty('deleted')) {
						eq(alias + '.deleted', false)
					}
				}

				switch (method) {
					case 'eq':
						if (val?.equalsIgnoreCase('null')) {
							isNull(property)
						}
						else if (infoForProperty.convertValueForProperty(prop.toString(), val.toString()) != null) {
							eq(property, infoForProperty.convertValueForProperty(prop.toString(), val.toString()))
						}
						break
					case 'ne':
						if (val?.equalsIgnoreCase('null')) {
							isNotNull(property)
						}
                        else if (infoForProperty.convertValueForProperty(prop.toString(), val.toString()) != null) {
							eq(property, infoForProperty.convertValueForProperty(prop.toString(), val.toString()))
						}
						break
					case 'gt':
					case 'lt':
					case 'ge':
					case 'le':
                        if (infoForProperty.convertValueForProperty(prop.toString(), val.toString()) != null) {
						    "${method}"(property, infoForProperty.convertValueForProperty(prop.toString(), val.toString()))
                        }
						break
				}
			}
		}
	}

	/**
	 * Allows for the creation and updating of records
	 * @param domain The name of the domain class
	 * @param id The id of the existing record, if null, a new record is created
	 * @param params The fields that have to be updated with the given values
	 * @return The record after the changes have been persisted to the database
	 * @throws Exception
	 */
	Object saveToDomain(String domain, Long id, Map params) throws Exception {
		String method = (id == null) ? 'PUT' : 'POST'
		DomainClassInfo domainClassInfo = new DomainClassInfo(grailsApplication, domain)
		List<String> allowed = getAllowed(domainClassInfo, method)
		Map validProperties = params.subMap(params.keySet().intersect(allowed)).findAll { it.value instanceof String }

		Class domainClass = domainClassInfo.getDomainClass()
		Object instance = id ? domainClass.findById(params.id) : domainClass.newInstance()

		validProperties.each { property, value ->
			if (property.toString().contains('.')) {
				instance.updateForApi(property, value)
			}
			else {
				instance."${property}" = domainClassInfo.convertValueForProperty(property.toString(), value.toString())
			}
		}

		instance.save(flush: true, failOnError: true)

		return instance
	}

	/**
	 * Allows a record to be deleted, if allowed through the API
	 * @param domain The name of the domain class
	 * @param id The id of the record to be deleted
	 * @return Whether the deletion was successful
	 */
	boolean deleteRecord(String domain, Long id) {
		DomainClassInfo domainClassInfo = new DomainClassInfo(grailsApplication, domain)
		if (apiActionIsAllowed(domainClassInfo, 'DELETE') && (id != null)) {
			def instance = domainClassInfo.getDomainClass().findById(id)
			return instance.delete()
		}

		return false
	}

	/**
	 * Find out which domain class properties are allowed through the API for the given HTTP method
	 * @param domainClassInfo The info object on the domain class in question
	 * @param httpMethod The HTTP method in question, either GET, PUT, POST or DELETE
	 * @return A list with the allowed properties
	 */
	private static List<String> getAllowed(DomainClassInfo domainClassInfo, String httpMethod = 'GET') {
		List<String> apiAllowed = []
		String propertyName = (httpMethod.equals('GET')) ? 'apiAllowed' : 'apiPostPut'

		if (apiActionIsAllowed(domainClassInfo, httpMethod) && domainClassInfo.hasProperty(propertyName)) {
			apiAllowed = (List<String>) domainClassInfo.getDomainClass()."${propertyName}"
		}

		return apiAllowed
	}

	/**
	 * Find out whether the current API action is allowed with the given HTTP method
	 * @param domainClassInfo The info object on the domain class in question
	 * @param httpMethod The HTTP method in question, either GET, PUT, POST or DELETE
	 * @return Whether it is allowed
	 */
	private static boolean apiActionIsAllowed(DomainClassInfo domainClassInfo, String httpMethod = 'GET') {
		return domainClassInfo.foundDomainClass() && domainClassInfo.hasProperty('apiActions') &&
				domainClassInfo.getDomainClass().apiActions?.contains(httpMethod)
	}
}
