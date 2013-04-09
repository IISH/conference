package org.iisg.eca.domain

/**
 * Domain class of table holding all participants (users) who signed up for an event date
 */
class ParticipantDate extends EventDateDomain {
    private static final String QUERY_LOWER_FEE_NOT_ANSWERED = "lowerFeeNotAnswered";
    
    User user
    ParticipantState state
    FeeState feeState
    Date dateAdded = new Date()
    boolean invitationLetter = false
    boolean invitationLetterSent = false
    boolean lowerFeeRequested = false
    boolean lowerFeeAnswered = false
    String lowerFeeText
    boolean student = false
    boolean studentConfirmed = false
    boolean award = false

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
        dateAdded               column: 'date_added'
        invitationLetter        column: 'invitation_letter'
        invitationLetterSent    column: 'invitation_letter_sent'
        lowerFeeRequested       column: 'lower_fee_requested'
        lowerFeeAnswered        column: 'lower_fee_answered'
        lowerFeeText            column: 'lower_fee_text'
        student                 column: 'student'
        studentConfirmed        column: 'student_confirmed'
        award                   column: 'award'

        extras                  joinTable: 'participant_date_extra'
        participantVolunteering cascade: 'all-delete-orphan'
    }

    static constraints = {
        lowerFeeText    nullable: true, maxSize: 255
    }

    static namedQueries = {
        paperAccepted { date ->
            user {
                papers {
                    eq('state.id', 2L)
                    eq('date.id', date.id)
                }
            }
        }

        paperInConsideration { date ->
            user {
                papers {
                    eq('state.id', 4L)
                    eq('date.id', date.id)
                }
            }
        }

        paperNotAccepted { date ->
            user {
                papers {
                    eq('state.id', 3L)
                    eq('date.id', date.id)
                }
            }
        }
        
        lowerFeeNotAnswered { date -> 
            eq('lowerFeeRequested', true)
            eq('lowerFeeAnswered', false)
        }
    }
    
    void updateByQueryType(String queryType) {
        switch (queryType) {
            case QUERY_LOWER_FEE_NOT_ANSWERED:
                lowerFeeRequested = true
                lowerFeeAnswered = true
                break
        }
    }

    @Override
    String toString() {
        user.toString()
    }
}

