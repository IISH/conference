package org.iisg.eca.domain

import groovy.sql.Sql
/**
 * Domain class of table holding all participants (users) who signed up for an event date
 */
class ParticipantDate extends EventDateDomain {
    def dataSource
    
    private static final String QUERY_STUDENT_LOWER_FEE_NOT_ANSWERED = "studentLowerFeeNotAnswered"
    private static final String QUERY_NO_STUDENT_LOWER_FEE_NOT_ANSWERED = "noStudentLowerFeeNotAnswered"
    
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

    void updateByQueryType(String queryType) {
        // Workaround for Hibernate exception "two open sessions" when using Quartz
        Sql sql = new Sql(dataSource)
        
        switch (queryType) {
            case QUERY_STUDENT_LOWER_FEE_NOT_ANSWERED:
            case QUERY_NO_STUDENT_LOWER_FEE_NOT_ANSWERED:
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

