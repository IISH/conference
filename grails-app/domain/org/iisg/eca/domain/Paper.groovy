package org.iisg.eca.domain

class Paper extends EventDateDomain {
    User user
    PaperState state
    String title
    String coAuthors
    String abstr
    String comment
    Network networkProposal
    String sessionProposal
    String proposalDescription
    String fileName
    String contentType
    Long fileSize
    byte[] file
    String equipmentComment

    static belongsTo = [User, PaperState, Network]
    static hasMany = [equipment: Equipment]

    static mapping = {
        table 'papers'
        version false

        id                  column: 'paper_id'
        state               column: 'paper_state_id'
        title               column: 'title'
        coAuthors           column: 'co_authors'
        abstr               column: 'abstract',             type: 'text'
        comment             column: 'comment',              type: 'text'
        networkProposal     column: 'network_proposal_id'
        sessionProposal     column: 'session_proposal'
        proposalDescription column: 'proposal_description', type: 'text'
        fileName            column: 'filename'
        contentType         column: 'content_type'
        fileSize            column: 'filesize'
        file                column: 'file',                 sqlType: 'mediumblob'
        equipmentComment    column: 'equipment_comment',    type: 'text'

        equipment           joinTable: 'paper_equipment'
    }

    static constraints = {
        title               blank: false,   maxSize: 500
        coAuthors           nullable: true, maxSize: 500
        abstr               nullable: true
        comment             nullable: true
        sessionProposal     nullable: true, maxSize: 500
        proposalDescription nullable: true
        fileName            nullable: true, maxSize: 500
        contentType         nullable: true, maxSize: 100
        fileSize            nullable: true
        file                nullable: true
        equipmentComment    nullable: true
    }

    @Override
    String toString() {
        title
    }
}
