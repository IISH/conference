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
        //id composite: ['participantDate', 'volunteering', 'network']
        version false

        id              column: 'participant_volunteering_temp_id'
        participantDate column: 'participant_date_id'
        volunteering    column: 'volunteering_id'
        network         column: 'network_id'

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
			'network.id',
	]

    /*Long getId() {
        if (participantDate?.id && volunteering?.id && network?.id) {
            "${participantDate.id}${volunteering.id}${network.id}".toLong()
        }
        else {
            null
        }
    }*/

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
