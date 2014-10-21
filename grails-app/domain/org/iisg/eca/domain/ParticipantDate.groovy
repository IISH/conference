package org.iisg.eca.domain

import grails.converters.JSON

/**
 * Domain class of table holding all participants (users) who signed up for an event date
 */
class ParticipantDate extends EventDateDomain {
    User user
    ParticipantState state
    FeeState feeState
    Long paymentId
    Date dateAdded = new Date()
    boolean invitationLetter = false
    boolean invitationLetterSent = false
    boolean lowerFeeRequested = false
    boolean lowerFeeAnswered = false
    String lowerFeeText
    boolean emailPaymentInfo = false
    boolean emailSessionInfo = false
    boolean emailSessionChairInfo = false
    boolean student = false
    boolean studentConfirmed = false
    boolean award = false
    String extraInfo
	User addedBy
	boolean deleted = false

    static belongsTo = [User, ParticipantState, FeeState]
	static hasMany = [  extras: Extra,
	                    participantVolunteering: ParticipantVolunteering,
	                    orders: Order,
	                    accompanyingPersons: String]

    static mapping = {
        table 'participant_date'
        version false
        // Causes ArrayIndexOutOfBoundsException: sort participantVolunteering: 'volunteering'

        id                      column: 'participant_date_id'
        user                    column: 'user_id'
        state                   column: 'participant_state_id'
        feeState                column: 'fee_state_id'
        paymentId               column: 'payment_id'
        dateAdded               column: 'date_added'
        invitationLetter        column: 'invitation_letter'
        invitationLetterSent    column: 'invitation_letter_sent'
        lowerFeeRequested       column: 'lower_fee_requested'
        lowerFeeAnswered        column: 'lower_fee_answered'
        lowerFeeText            column: 'lower_fee_text'
        emailPaymentInfo        column: 'email_payment_info'
        emailSessionInfo        column: 'email_session_info'
        emailSessionChairInfo   column: 'email_session_chair_info'
        student                 column: 'student'
        studentConfirmed        column: 'student_confirmed'
        award                   column: 'award'
        extraInfo               column: 'extra_info',   type: 'text'
	    addedBy                 column: 'added_by'
	    deleted                 column: 'deleted'

        extras                  joinTable: 'participant_date_extra'
        participantVolunteering cascade: 'all-delete-orphan'
	    accompanyingPersons     joinTable: [name:   'accompanying_persons',
	                                        key:    'participant_date_id',
	                                        column: 'name']
    }

    static constraints = {
        paymentId       nullable: true
        lowerFeeText    nullable: true, maxSize: 255
        extraInfo       nullable: true
	    addedBy         nullable: true
    }

	static hibernateFilters = {
		dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
		hideDeleted(condition: 'deleted = 0', default: true)
	}

    static apiActions = ['GET', 'POST', 'PUT', 'DELETE']

    static apiAllowed = [
            'id',
            'user.id',
            'state.id',
            'feeState.id',
            'paymentId',
            'invitationLetter',
            'lowerFeeRequested',
            'lowerFeeText',
            'student',
            'award',
		    'accompanyingPersons',
            'extras.id',
            'participantVolunteering.id',
		    'addedBy.id'
    ]

    static apiPostPut = [
		    'paymentId',
		    'invitationLetter',
		    'lowerFeeRequested',
		    'student',
		    'award',
		    'accompanyingPersons',
            'extras.id',
		    'state.id',
		    'feeState.id',
		    'user.id',
		    'addedBy.id'
    ]

	void softDelete() {
		deleted = true
	}

    void updateForApi(String property, String value) {
        switch (property) {
            case 'extras.id':
                this.extras?.clear()
                this.save(flush: true)
                value.split(';').each { extraId ->
                    if (extraId.toString().isLong()) {
                        Extra extra = Extra.findById(extraId.toString().toLong())
                        if (extra) {
                            this.addToExtras(extra)
                        }
                    }
                }
                break
	        case 'accompanyingPersons':
		        this.accompanyingPersons?.clear()
		        this.save(flush: true)
		        this.accompanyingPersons = JSON.parse(value)*.trim()
		        break
	        case 'state.id':
		        ParticipantState state = (value.isLong()) ? ParticipantState.findById(value.toLong()) : null
		        if (state) {
			        this.state = state
		        }
				break
	        case 'feeState.id':
		        FeeState state = (value.isLong()) ? FeeState.findById(value.toLong()) : null
		        if (state) {
			        this.feeState = state
		        }
		        break
	        case 'user.id':
		        User user = (value.isLong()) ? User.findById(value.toLong()) : null
		        if (user) {
			        this.user = user
		        }
		        break
	        case 'addedBy.id':
		        User addedBy = (value.isLong()) ? User.findById(value.toLong()) : null
		        if (addedBy) {
			        this.addedBy = addedBy
		        }
		        break
        }
    }

	/**
	 * Sorting here instead of using the database, to overcome issues
	 * @return The participant volunteerings sorted
	 */
	List<ParticipantVolunteering> getParticipantVolunteeringSorted() {
		if (participantVolunteering) {
			participantVolunteering.sort { ParticipantVolunteering pv1, ParticipantVolunteering pv2 ->
				if (pv1.volunteering.id == pv2.volunteering.id) {
					pv1.network.name <=> pv2.network.name
				}
				else {
					pv1.volunteering.description <=> pv2.volunteering.description
				}
			}
		}
		else {
			[]
		}
	}

	/**
	 * The order of the participant, by payment id
	 * @return The order in question
	 */
    Order findOrder() {
        Order.get(paymentId)
    }

    @Override
    String toString() {
        user.toString()
    }
}

