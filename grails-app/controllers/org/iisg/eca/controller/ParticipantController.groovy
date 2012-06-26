package org.iisg.eca.controller

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Extra
import org.iisg.eca.domain.Network
import org.iisg.eca.domain.FeeState
import org.iisg.eca.domain.Equipment
import org.iisg.eca.domain.PaperState
import org.iisg.eca.domain.Volunteering
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.SessionDateTime
import org.iisg.eca.domain.ParticipantState
import org.iisg.eca.domain.ParticipantVolunteering

import grails.converters.JSON
import grails.validation.ValidationException
import org.springframework.web.multipart.commons.CommonsMultipartFile

/**
 * Controller responsible for handling requests on participants
 */
class ParticipantController {
    /**
     * Information about the current page
     */
    def pageInformation

    /**
     * Service taking care of participant information
     */
    def participantService

    /**
     * Service taking care of exporting the participants paper
     */
    def exportService

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
    }

    /**
     * Shows all inventations requests made by participants
     */
    def inventations() {
        forward(controller: "dynamicPage", action: "get")
    }

    /**
     * Shows all lower fee requests made by participants
     */
    def lowerFee() {
        forward(controller: "dynamicPage", action: "get")
    }

    /**
     * Shows all participants that showed interest for one of the extras
     */
    def extras() {
        forward(controller: "dynamicPage", action: "get")
    }

    /**
     * Shows a list of all participants who signed up for the current event date
     * and offers users to filter the results
     */
    def list() {
        // When the page is first requested, show no results yet: start with an empty map
        Map participants = [:]

        // If the user clicked on search, let the participant service get all participants from the database
        if (params['filter-type']) {
            participants = participantService.getParticipants(params)
        }

        // Return a map with participants, the characters (alphabet) and
        // participant states including the number of participants for each state
        render(view: "list", model: [   participants:   participants,
                                        alphabet:       participants.keySet(),
                                        states:         participantService.getParticipantCounts()])
    }

    /**
     * Shows all participant information and allows for editing
     */
    def show() {
        // We need an id, check for the id
        if (!params.id) {
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        User user = User.get(params.id)

        // We also need a user to be able to show something
        if (!user) {
            flash.message = g.message(code: 'default.not.found.message', args: [message(code: 'user.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // Try to look up this user as a participant for the current event date
        ParticipantDate participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)

        // The 'save' button was clicked, save all data
        if (request.post) {
            try {
                // Save all user information
                bindData(user, params, [include: ['title', 'firstName', 'lastName', 'gender', 'organisation',
                        'department', 'email', 'address', 'city', 'country', 'phone', 'mobile', 'extraInfo']], "User")

                if (!participant && params['add-to-date']?.equals('add')) {
                    // Try to find out if this participant has been deleted before or is filtered for some other reason
                    ParticipantDate.withoutHibernateFilters  {
                        participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)
                    }

                    // If we found the filtered participant, undo the deletion
                    if (participant) {
                        participant.deleted = false
                    }
                    else {
                        // This user is not a participant yet, but the user indicated he/she wants to make him/her one
                        participant = new ParticipantDate(user: user, state: ParticipantState.get(0), feeState: FeeState.get(0))
                    }

                    participant.save(failOnError: true)
                    user.save(failOnError: true)
                }
                else if (!participant) {
                    // Not a participant, so just save the user information
                    user.save(failOnError: true)
                }
                else {
                    // He/she is a participant, save all of that information as well
                    bindData(participant, params, [include: ['invitationLetter', 'invitationLetterSent', 'lowerFeeRequested',
                            'lowerFeeAnswered', 'state', 'feeState']], "ParticipantDate")

                    // Remove all extras the participant is interested in and save all new information
                    participant.extras.clear()
                    params."ParticipantDate.extras".each { extraId ->
                        participant.addToExtras(Extra.get(extraId))
                    }

                    // Remove all date/times the user is not present and save all new information
                    // However, we are only interested in the dates/times the participant is NOT present
                    user.dateTimesNotPresent.clear()
                    List<SessionDateTime> sessionDateTimes = SessionDateTime.list()
                    params.present.each { dateTimeId ->
                        sessionDateTimes.remove(sessionDateTimes.find { dateTimeId.isLong() && (it.id == dateTimeId.toLong()) })
                    }
                    user.dateTimesNotPresent.addAll(sessionDateTimes)

                    // Remove all volunteering offers from the participant and save all new information
                    int i = 0
                    participant.participantVolunteering.clear()
                    participant.save(failOnError: true, flush: true)
                    while (params["ParticipantVolunteering_${i}"]) {
                        ParticipantVolunteering pv = new ParticipantVolunteering()
                        bindData(pv, params, [include: ['volunteering', 'network']], "ParticipantVolunteering_${i}")
                        participant.addToParticipantVolunteering(pv)
                        i++
                    }

                    // Check which papers have to be deleted, try to soft delete them
                    String[] ids = params["to-be-deleted"].split(';')
                    ids.each { idToDelete ->
                        if (idToDelete.isLong()) {
                            Paper paperToDelete = Paper.findById(idToDelete.toLong())
                            paperToDelete?.softDelete()
                            paperToDelete?.save(failOnError: true)
                        }
                    }

                    // Now try to save the paper information, one paper at the time
                    i = 0
                    while (params["Paper_${i}"]) {
                        Paper paper = null

                        // Try to look up the paper in the database
                        if (params["Paper_${i}.id"]?.isLong()) {
                            paper = Paper.findByUserAndId(user, params.long("Paper_${i}.id"))
                        }

                        // Not found, then create a new paper
                        if (!paper) {
                            paper = new Paper(state: PaperState.get(0))
                            user.addToPapers(paper)
                        }

                        // Save all paper information
                        bindData(paper, params, [include: ['title', 'abstr', 'coAuthors', 'state', 'comment',
                                'networkProposal', 'sessionProposal', 'proposalDescription',
                                'equipmentComment']], "Paper_${i}")

                        // If a paper file is uploaded, save all file information
                        CommonsMultipartFile file = (CommonsMultipartFile) params["Paper_${i}.file"]
                        if (file?.size > 0) {
                            paper.fileSize = file.size
                            paper.contentType = file.contentType
                            paper.fileName = file.originalFilename
                            paper.file = file.bytes
                        }

                        // Remove all equipment and save all new equipment information for this paper
                        paper.equipment?.clear()
                        params["Paper_${i}.equipment"].each { equipmentId ->
                            if (equipmentId.isLong()) {
                                paper.addToEquipment(Equipment.get(equipmentId.toLong()))
                            }
                        }

                        // Save the paper
                        paper.save(failOnError: true)
                        i++
                    }

                    // Save the user and the participant
                    user.save(failOnError: true)
                    participant.save(failOnError: true)
                }

                // We arrived here, so everything should be fine
                // Go back to the previous back with an update message
                flash.message = g.message(code: 'default.updated.message', args: [g.message(code: 'participantDate.label')])
                redirect(uri: eca.createLink(action: 'list', noBase: true))
                return
            }
            catch (ValidationException ve) {
                // Validation failed, just send back the page and show all errors
                render(view: "form", model: [   user: user,
                                                participant: participant,
                                                papers: Paper.findAllByUser(user),
                                                volunteering: Volunteering.list(),
                                                networks: Network.list(),
                                                paperStates: PaperState.list(),
                                                equipmentList: Equipment.list(),
                                                participantIds: participantService.getParticipantsWithFilters(params).collect { it[0] }])
            }
        }

        // Show the participant data
        render(view: "form", model: [   user: user,
                                        participant: participant,
                                        papers: Paper.findAllByUser(user),
                                        volunteering: Volunteering.list(),
                                        networks: Network.list(),
                                        paperStates: PaperState.list(),
                                        equipmentList: Equipment.list(),
                                        participantIds: participantService.getParticipantsWithFilters(params).collect { it[0] }])
    }

    /**
     * Removes the user as a participant from the current event date
     */
    def delete() {
        // Of course we need an id of the user
        if (params.id) {
            User user = User.get(params.id)
            ParticipantDate participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)
            participant?.softDelete()

            // Try to remove the user as a participant, send back a success or failure message
            if (participant?.save(flush: true)) {
                flash.message =  message(code: 'default.deleted.message', args: [message(code: 'participantDate.label')])
            }
            else {
                flash.message =  message(code: 'default.not.deleted.message', args: [message(code: 'participantDate.label')])
            }
        }
        else {
            flash.message =  message(code: 'default.no.id.message')
        }

        // The previous page probably does not exist anymore, so send back to the participant listing page
        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }

    /**
     * Tries to download the uploaded paper
     */
    def downloadPaper() {
        Paper paper = Paper.get(params.id)

        // Let the export service deal with returning the uploaded paper
        if (paper) {
            exportService.getPaper(paper, response)
        }
    }

    /**
     * Tries to remove the uploaded paper
     * (AJAX call)
     */
    def removePaper() {
        if (request.xhr) {
            Paper paper = Paper.get(params.long('paper-id'))

            // We found the paper, now just set everything to null
            if (paper) {
                paper.file = null
                paper.fileName = null
                paper.fileSize = null
                paper.contentType = null
                paper.save(flush: true)
            }

            // That's it
            Map returnMap = [success: true]
            render returnMap as JSON
        }
    }
}
