package org.iisg.eca.domain

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * Domain class of table holding all chairs of a network
 */
class NetworkChair implements Serializable {
    Network network
    User chair
    boolean isMainChair = false
	boolean votedAdvisoryBoard = false

    static belongsTo = [Network, User]

    static mapping = {
        table 'networks_chairs'
        version false

        id                  column: 'network_chair_id'
        network             column: 'network_id',       fetch: 'join'
        chair               column: 'user_id',          fetch: 'join'
        isMainChair         column: 'is_main_chair'
	    votedAdvisoryBoard  column: 'voted_advisory_board'
    }

    static constraints = {
        network     unique: 'chair'
    }

    static apiActions = ['GET', 'POST']

    static apiAllowed = [
            'id',
		    'chair.id',
            'network.id',
            'chair',
            'isMainChair',
		    'votedAdvisoryBoard'
    ]

	static apiPostPut = [
			'votedAdvisoryBoard'
	]

    @Override
    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append network
        builder.append chair
        builder.toHashCode()
    }

    @Override
    boolean equals(other) {
        if (other == null || !(other instanceof NetworkChair)) {
            return false
        }

        def builder = new EqualsBuilder()
        builder.append network, other.network
        builder.append chair, other.chair
        builder.isEquals()
    }

    @Override
    String toString() {
        if (isMainChair) {
            "$chair: ${chair.email} (Main chair)"
        }
        else {
            "$chair: ${chair.email}"
        }
    }
}
