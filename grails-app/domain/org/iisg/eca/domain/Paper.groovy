package org.iisg.eca.domain

import java.math.RoundingMode

/**
 * Domain class of table holding all papers
 */
class Paper extends EventDateDomain {
    User user
    PaperState state
    Session session
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
    boolean mailPaperState = true
	User addedBy

    static belongsTo = [User, PaperState, Session, Network]
    static hasMany = [equipment: Equipment]

    static mapping = {
        table 'papers'
        version false

        id                  column: 'paper_id'
        state               column: 'paper_state_id'
        session             column: 'session_id'
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
        mailPaperState      column: 'mail_paper_state'
	    addedBy             column: 'added_by'

        equipment           joinTable: 'paper_equipment'
    }

    static constraints = {
        session             nullable: true
        title               blank: false,   maxSize: 500, validator: { val, obj ->
            Integer maxPapers = Setting.getSetting(Setting.MAX_PAPERS_PER_PERSON_PER_SESSION)?.value?.toInteger()
            if (maxPapers && !obj?.id && (obj?.user?.papers?.findAll { !it?.deleted && (it?.date?.id == pageInformation.date.id) }?.size() > maxPapers)) {
                "paper.validation.max.message"
            }
        }
        coAuthors           nullable: true, maxSize: 500
        abstr               blank: false
        comment             nullable: true
        networkProposal     nullable: true
        sessionProposal     nullable: true, maxSize: 500
        proposalDescription nullable: true
        fileName            nullable: true, maxSize: 500
        contentType         nullable: true, maxSize: 100
        fileSize            nullable: true
        file                nullable: true
        equipmentComment    nullable: true
	    addedBy             nullable: true
    }

    static apiActions = ['GET', 'POST', 'PUT']

    static apiAllowed = [
            'id',
            'user.id',
            'state.id',
            'session.id',
            'title',
            'coAuthors',
            'abstr',
            'networkProposal.id',
            'sessionProposal',
            'proposalDescription',
            'fileName',
            'contentType',
            'fileSize',
            'equipmentComment',
            'equipment.id',
		    'addedBy.id'
    ]

	static apiPostPut = [
			'title',
			'coAuthors',
			'abstr',
			'sessionProposal',
			'equipmentComment',
			'user.id',
			'state.id',
			'session.id',
			'networkProposal.id',
			'equipment.id',
			'addedBy.id'
	]

	void updateForApi(String property, String value) {
		switch (property) {
			case 'user.id':
				User user = User.get(value.toLong())
				if (user) {
					this.user = user
				}
				break
			case 'state.id':
				PaperState state = PaperState.findById(value.toLong())
				if (state) {
					this.state = state
				}
				break
			case 'session.id':
				Session session = Session.findById(value.toLong())
				if (session) {
					this.session = session
				}
				break
			case 'networkProposal.id':
				Network networkProposal = Network.findById(value.toLong())
				if (networkProposal) {
					this.networkProposal = networkProposal
				}
				break
			case 'equipment.id':
				this.equipment?.clear()
				this.save(flush: true)
				value.split(';').each { equipmentId ->
					if (equipmentId.toString().isLong()) {
						Equipment equipment = Equipment.findById(equipmentId.toString().toLong())
						if (equipment) {
							this.addToEquipment(equipment)
						}
					}
				}
				break
			case 'addedBy.id':
				User addedBy = User.findById(value.toLong())
				if (addedBy) {
					this.addedBy = addedBy
				}
				break
		}
	}

    /**
     * Returns the file size in a human friendly readable way
     * @return The file size
     */
    String getReadableFileSize() {
        if (!fileSize) {
            return "0 bytes"
        }

        if (fileSize/1024 > 1) {
            if (fileSize/1048576 > 1) {
                return "${(fileSize/1048576).setScale(2, RoundingMode.HALF_UP)} MB"
            }
            return "${(fileSize/1024).setScale(2, RoundingMode.HALF_UP)} KB"
        }
        return "${fileSize} bytes"
    }

    /**
     * Updates the mailPaperState when the state of this paper has been changed
     */
    def beforeUpdate() {
        if (isDirty('state')) {
            mailPaperState = true
        }
    }

    @Override
    String toString() {
        title
    }
}
