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

	private static final Map<String, List<String>> QUERY_TYPE_COMBINATIONS_ADD = [
			'sessionAccepted'                  : ['sessionAcceptedOrganizers', 'sessionAcceptedCreators'],
			'sessionInConsideration'           : ['sessionInConsiderationOrganizers', 'sessionInConsiderationCreators'],
			'sessionNotAccepted'               : ['sessionNotAcceptedOrganizers', 'sessionNotAcceptedCreators'],
			'sessionAcceptedNotAnswered'       : ['sessionAcceptedOrganizersNotAnswered', 'sessionAcceptedCreatorsNotAnswered'],
			'sessionInConsiderationNotAnswered': ['sessionInConsiderationOrganizersNotAnswered', 'sessionInConsiderationCreatorsNotAnswered'],
			'sessionNotAcceptedNotAnswered'    : ['sessionNotAcceptedOrganizersNotAnswered', 'sessionNotAcceptedCreatorsNotAnswered'],
	]

	private static final Map<String, List<String>> QUERY_TYPE_COMBINATIONS_SUBTRACT = [
			'unconfirmedPayments'      : ['allParticipantsSoftState', 'confirmedPayments'],
			'unconfirmedPaymentsSoft'  : ['allParticipantsSoftState', 'confirmedPayments', 'allUnconfirmedCashPayments'],
			'unconfirmedCashPayments'  : ['allUnconfirmedCashPayments', 'confirmedPayments'],
			'unconfirmedBankPayments'  : ['allUnconfirmedBankPayments', 'confirmedPayments'],
			'unconfirmedOnlinePayments': ['allUnconfirmedOnlinePayments', 'confirmedPayments'],
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
		boolean add = true

		if (QUERY_TYPE_COMBINATIONS_ADD.containsKey(this.queryType)) {
			queryTypes = QUERY_TYPE_COMBINATIONS_ADD.get(this.queryType)
		}
		else if (QUERY_TYPE_COMBINATIONS_SUBTRACT.containsKey(this.queryType)) {
			queryTypes = QUERY_TYPE_COMBINATIONS_SUBTRACT.get(this.queryType)
			add = false
		}

		List results = []
		int i = 0
		for (String queryType : queryTypes) {
			NamedCriteriaProxy criteria = User."$queryType"(this.date)
			List namedQueryResults = (List) criteria(this.additionalCriteria)

			if (add || (i == 0)) {
				results.addAll(namedQueryResults)
			}
			else {
				results.removeAll(namedQueryResults)
			}

			i++
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
