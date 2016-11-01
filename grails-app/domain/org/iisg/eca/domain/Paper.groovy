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
	String typeOfContribution
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
	boolean deleted = false

	static belongsTo = [User, PaperState, Session, Network]
    static hasMany = [equipment: Equipment]

    static mapping = {
        table 'papers'
        version false

        id                  column: 'paper_id'
	    user				column: 'user_id',              fetch: 'join'
        state               column: 'paper_state_id',       fetch: 'join'
        session             column: 'session_id',           fetch: 'join'
        title               column: 'title'
        coAuthors           column: 'co_authors'
        abstr               column: 'abstract',             type: 'text'
		typeOfContribution	column: 'type_of_contribution'
        comment             column: 'comment',              type: 'text'
        networkProposal     column: 'network_proposal_id',  fetch: 'join'
        sessionProposal     column: 'session_proposal'
        proposalDescription column: 'proposal_description', type: 'text'
        fileName            column: 'filename'
        contentType         column: 'content_type'
        fileSize            column: 'filesize'
        file                column: 'file',                 sqlType: 'mediumblob'
        equipmentComment    column: 'equipment_comment',    type: 'text'
        mailPaperState      column: 'mail_paper_state'
	    addedBy             column: 'added_by'
	    deleted             column: 'deleted'

        equipment           joinTable: 'paper_equipment'
    }

    static constraints = {
        session             nullable: true
        title               blank: false,   maxSize: 500
        coAuthors           nullable: true, maxSize: 500
        abstr               blank: false
		typeOfContribution	nullable: true,	maxSize: 100
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

	static hibernateFilters = {
		dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
		hideDeleted(condition: 'deleted = 0', default: true)
	}

    static apiActions = ['GET', 'POST', 'PUT', 'DELETE']

    static apiAllowed = [
            'id',
            'user.id',
            'state.id',
            'session.id',
            'title',
            'coAuthors',
            'abstr',
			'typeOfContribution',
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
			'typeOfContribution',
			'sessionProposal',
			'equipmentComment',
			'user.id',
			'state.id',
			'session.id',
			'networkProposal.id',
			'equipment.id',
			'addedBy.id'
	]

	void softDelete() {
		deleted = true
	}

	void updateForApi(String property, String value) {
		switch (property) {
			case 'user.id':
				User user = (value.isLong()) ? User.get(value.toLong()) : null
				if (user) {
					this.user = user
				}
				break
			case 'state.id':
				PaperState state = (value.isLong()) ? PaperState.findById(value.toLong()) : null
				if (state) {
					this.state = state
				}
				break
			case 'session.id':
				Session session = (value.isLong()) ? Session.findById(value.toLong()) : null
				this.session = session
				break
			case 'networkProposal.id':
                log.warn("Setting network proposal for paper with name ${title} and id ${id}. Value is ${value} .")
				Network networkProposal = (value.isLong()) ? Network.findById(value.toLong()) : null
				if (networkProposal) {
					this.networkProposal = networkProposal
                    log.warn("Setting network proposal for paper with name ${title} and id ${id}. Network proposal is ${networkProposal} .")
				}
                else {
                    log.warn("Setting network proposal for paper with name ${title} and id ${id}. Network appears to be null: ${networkProposal} .")
                }
				break
			case 'addedBy.id':
				User addedBy = (value.isLong()) ? User.findById(value.toLong()) : null
				if (addedBy) {
					this.addedBy = addedBy
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
	 * Removes the paper file by setting all related columns to null
	 * @return Whether the paper was successfully removed
	 */
	boolean removePaperFile() {
		this.file = null
		this.fileName = null
		this.fileSize = null
		this.contentType = null

		this.save()
	}

	/**
	 * Check if a paper file was uploaded
	 * @return true if a paper was uploaded
	 */
	boolean hasPaperFile() {
		return ((this.fileSize != null) && (this.fileSize > 0))
	}

	/**
	 * Whether the paper presentation requires the equipment with the given id
	 * @param equipmentId The equipment id in question
	 * @return True if the paper presentation requires the given equipment
	 */
	boolean hasEquipmentWithId(long equipmentId) {
		return equipment?.find { it.id == equipmentId }
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
