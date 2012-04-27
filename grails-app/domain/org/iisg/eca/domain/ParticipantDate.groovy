package org.iisg.eca.domain

class ParticipantDate extends EventDateDomain implements Serializable {
    User user
    ParticipantState state
    FeeState feeState
    Date dateAdded = new Date()
    boolean invitationLetter = false
    boolean invitationLetterSent = false
    boolean lowerFeeRequested = false
    boolean lowerFeeAnswered = false
    String lowerFeeText

    static belongsTo = [User, ParticipantState, FeeState]
    static hasMany = [extras: Extra]

    static mapping = {
        table 'participant_date'
        version false

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

        extras  joinTable: 'participant_date_extra'
    }

    static constraints = {
        lowerFeeText    nullable: true, maxSize: 255
    }

    @Override
    String toString() {
        "${date.yearCode}: ${state.state}"
    }
}
