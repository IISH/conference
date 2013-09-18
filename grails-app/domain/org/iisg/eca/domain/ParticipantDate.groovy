package org.iisg.eca.domain

import groovy.sql.Sql
/**
 * Domain class of table holding all participants (users) who signed up for an event date
 */
class ParticipantDate extends EventDateDomain {
    def dataSource
    
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
        allParticipants { date ->
            'in'('state.id', [ParticipantState.PARTICIPANT_DATA_CHECKED, ParticipantState.PARTICIPANT])

            user {
                eq('deleted', false)
                eq('date.id', date.id)

                order('lastName', "asc")
                order('firstName', "asc")
            }
        }
        
        paperAccepted { date ->
            allParticipants(date)
            
            user {
                papers {
                    eq('state.id', PaperState.PAPER_ACCEPTED)
                    eq('date.id', date.id)
                }
            }
        }

        paperInConsideration { date ->
            allParticipants(date)
            
            user {
                papers {
                    eq('state.id', PaperState.PAPER_IN_CONSIDERATION)
                    eq('date.id', date.id)
                }
            }
        }

        paperNotAccepted { date ->
            allParticipants(date)
            
            user {
                papers {
                    eq('state.id', PaperState.PAPER_NOT_ACCEPTED)
                    eq('date.id', date.id)
                }
            }
        }
        
        lowerFeeNotAnswered { date -> 
            allParticipants(date)
            
            eq('lowerFeeRequested', true)
            eq('lowerFeeAnswered', false)
        }
    }
    
    void updateByQueryType(String queryType) {
        // Workaround for Hibernate exception "two open sessions" when using Quartz
        Sql sql = new Sql(dataSource)
        
        switch (queryType) {
            case QUERY_LOWER_FEE_NOT_ANSWERED:
                sql.executeUpdate("""
                    UPDATE  participant_date
                    SET     lower_fee_requested = 1,
                            lower_fee_answered = 1
                    WHERE   participant_date_id = ?
                """, [id])
                break
        }
    }

    @Override
    String toString() {
        user.toString()
    }
}

