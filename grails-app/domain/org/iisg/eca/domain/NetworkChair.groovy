package org.iisg.eca.domain

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * Domain class of table holding all chairs of a network
 */
class NetworkChair extends DefaultDomain implements Serializable {
    Long id
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

    static constraints = {
        network     unique: 'chair'
    }

    Long getId() {
        "${network.id}${chair.id}".toLong()
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

    @Override
    String toString() {
        if (isMainChair) {
            "${chair} (Main chair)"
        }
        else {
            chair
        }
    }
}
