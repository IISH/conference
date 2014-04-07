package org.iisg.eca.utils

import org.iisg.eca.domain.User
import org.iisg.eca.domain.EventDate

import org.codehaus.groovy.grails.orm.hibernate.cfg.NamedCriteriaProxy

/**
 * Allows for querying users using a specified query type (either a named query or a combination of named queries)
 */
class QueryTypeCriteriaBuilder {
	private EventDate date
	private String queryType
	private Closure additionalCriteria

	private static final Map<String, List<String>> QUERY_TYPE_COMBINATIONS = [
			'sessionAccepted'                  : ['sessionAcceptedOrganizers', 'sessionAcceptedCreators'],
			'sessionInConsideration'           : ['sessionInConsiderationOrganizers', 'sessionInConsiderationCreators'],
			'sessionNotAccepted'               : ['sessionNotAcceptedOrganizers', 'sessionNotAcceptedCreators'],
			'sessionAcceptedNotAnswered'       : ['sessionAcceptedOrganizersNotAnswered', 'sessionAcceptedCreatorsNotAnswered'],
			'sessionInConsiderationNotAnswered': ['sessionInConsiderationOrganizersNotAnswered', 'sessionInConsiderationCreatorsNotAnswered'],
			'sessionNotAcceptedNotAnswered'    : ['sessionNotAcceptedOrganizersNotAnswered', 'sessionNotAcceptedCreatorsNotAnswered'],
	]

	public QueryTypeCriteriaBuilder(EventDate date) {
		this.date = date
		this.queryType = 'allParticipants'
	}

	public QueryTypeCriteriaBuilder(EventDate date, String queryType) {
		this.date = date
		this.queryType = queryType
	}

	/**
	 * Additional criteria that will be added to the query type when building the query
	 * @param additionalCriteria The additional criteria to add
	 */
	public void setAdditionalCriteria(Closure additionalCriteria) {
		this.additionalCriteria = additionalCriteria
	}

	/**
	 * Returns the database results from the specified query type and additional criteria
	 * @return A list with User objects or other properties as specified in the additional criteria
	 */
	public List getResults() {
		List<String> queryTypes = [this.queryType]
		if (QUERY_TYPE_COMBINATIONS.containsKey(this.queryType)) {
			queryTypes = QUERY_TYPE_COMBINATIONS.get(this.queryType)
		}

		List results = []
		for (String queryType : queryTypes) {
			NamedCriteriaProxy criteria = User."$queryType"(this.date)
			List namedQueryResults = (List) criteria(this.additionalCriteria)
			results.addAll(namedQueryResults)
		}

		return results
	}

	/**
	 * Returns the unique database results from the specified query type and additional criteria
	 * @return A unique list with User objects or other properties as specified in the additional criteria
	 */
	public List getUniqueResults() {
		return getResults()?.unique()
	}
}
