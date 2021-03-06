package org.iisg.eca.domain

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * Domain class of table holding all participants who signed up as a volunteer
 */
class ParticipantVolunteering implements Serializable {
    ParticipantDate participantDate
    Volunteering volunteering
    Network network

    static belongsTo = [ParticipantDate, Volunteering, Network]

    static mapping = {
        table 'participant_volunteering'
        version false

        id              column: 'participant_volunteering_id'
        participantDate column: 'participant_date_id',          fetch: 'join'
        volunteering    column: 'volunteering_id',              fetch: 'join'
        network         column: 'network_id',                   fetch: 'join'

        sort    volunteering: 'description'
    }

    static apiActions = ['GET', 'POST', 'PUT', 'DELETE']

    static apiAllowed = [
		    'id',
            'participantDate.id',
            'volunteering.id',
            'network.id',
            'volunteering',
            'network',
		    'participantDate',
		    'participantDate.user'
    ]

	static apiPostPut = [
			'participantDate.id',
			'volunteering.id',
			'network.id'
	]

    static namedQueries = {
        sortedParticipantVolunteering { participantDateId ->
            eq('participantDate.id', participantDateId)
            volunteering {
                order('description', 'asc')
            }
            network {
                order('name', 'asc')
            }
        }
    }

	void updateForApi(String property, String value) {
		switch (property) {
			case 'participantDate.id':
				ParticipantDate participantDate = ParticipantDate.findById(value.toLong())
				if (participantDate) {
					this.participantDate = participantDate
				}
				break
			case 'volunteering.id':
				Volunteering volunteering = Volunteering.findById(value.toLong())
				if (volunteering) {
					this.volunteering = volunteering
				}
				break
			case 'network.id':
				Network network = Network.findById(value.toLong())
				if (network) {
					this.network = network
				}
				break
		}
	}

    @Override
    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append participantDate
        builder.append volunteering
        builder.append network
        builder.toHashCode()
    }

    @Override
    boolean equals(other) {
        if (other == null || !(other instanceof ParticipantVolunteering)) {
            return false
        }

        def builder = new EqualsBuilder()
        builder.append participantDate, other.participantDate
        builder.append volunteering, other.volunteering
        builder.append network, other.network
        builder.isEquals()
    }
    
    boolean equalsWithoutParticipant(other) {
        if (other == null || !(other instanceof ParticipantVolunteering)) {
            return false
        }

        def builder = new EqualsBuilder()
        builder.append volunteering, other.volunteering
        builder.append network, other.network
        builder.isEquals()
    }
}
