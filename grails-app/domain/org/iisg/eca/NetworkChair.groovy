package org.iisg.eca

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * Domain class of table holding all chairs of a network
 */
class NetworkChair implements Serializable {
    Network network
    User chair
    boolean isMainChair = false

    static belongsTo = [Network, User]

    static mapping = {
        table 'networks_chairs'
		id composite: ['network', 'chair']
		version false

        network     column: 'network_id'
        chair       column: 'user_id'
        isMainChair column: 'is_main_chair'
	}

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
}
