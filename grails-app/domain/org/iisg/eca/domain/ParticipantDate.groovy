package org.iisg.eca.domain

import org.iisg.eca.domain.payway.Order

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

    static belongsTo = [User, ParticipantState, FeeState]
    static hasMany = [extras: Extra, participantVolunteering: ParticipantVolunteering]

    static mapping = {
        table 'participant_date'
        version false
        sort participantVolunteering: 'volunteering'

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

        extras                  joinTable: 'participant_date_extra'
        participantVolunteering cascade: 'all-delete-orphan'
    }

    static constraints = {
        paymentId       nullable: true
        lowerFeeText    nullable: true, maxSize: 255
        extraInfo       nullable: true
    }

    static apiActions = ['GET', 'POST', 'PUT']

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
            'extras.id',
            'participantVolunteering.id'
    ]

    static apiPostPut = [
		    'paymentId',
		    'invitationLetter',
		    'lowerFeeRequested',
		    'student',
		    'award',
            'extras.id',
		    'state.id',
		    'feeState.id',
		    'user.id',
    ]

    void updateForApi(String property, String value) {
        switch (property) {
            case 'extras.id':
                this.extras.clear()
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
	        case 'state.id':
		        ParticipantState state = ParticipantState.findById(value.toLong())
		        if (state) {
			        this.state = state
		        }
				break
	        case 'feeState.id':
		        FeeState state = FeeState.findById(value.toLong())
		        if (state) {
			        this.feeState = state
		        }
		        break
	        case 'user.id':
		        User user = User.findById(value.toLong())
		        if (user) {
			        this.user = user
		        }
		        break
        }
    }

    Order findOrder() {
        Order order = Order.get(paymentId)
        order
    }

    @Override
    String toString() {
        user.toString()
    }
}

