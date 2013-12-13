package org.iisg.eca.service

import org.iisg.eca.domain.User
import org.iisg.eca.domain.EmailTemplate

import org.iisg.eca.utils.PageInformation

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.codehaus.groovy.grails.orm.hibernate.cfg.NamedCriteriaProxy

/**
 * Service that will filter out the email recipients based on filters set by the user and the template used
 */
class EmailRecipientsService {
    /**
     * In order to query the event date from which the request was initiated
     */
    PageInformation pageInformation

    /**
     * Returns a list with the user ids that need to receive the email as specified by the template
     * @param template The template that indicates the email type
     * @param filters The parameters as filled out by the users to filter on
     * @return A list with at least the user ids and depending on the template also ids of other records
     * (as specified by the call to <code>getAssociationsNames()</code> on the template)
     */
    List<Long[]> getRecipientsFor(EmailTemplate template, GrailsParameterMap filters) {
        Map<String, Boolean> filterMap = template.getFilterMap()
        NamedCriteriaProxy criteria

        // If only one user is chosen, different rules count
        boolean oneUserChosen = (filterMap['participant'] && filters.participant?.isLong())

        // The email template chosen could contain a query type (a named query to call)
        if (!oneUserChosen && template.queryTypeMultiple) {
            criteria = User."$template.queryTypeMultiple"(pageInformation.date)
        }
        else if (oneUserChosen && template.queryTypeOne) {
            criteria = User."$template.queryTypeOne"(pageInformation.date)
        }
        else {
            criteria = User.allParticipants(pageInformation.date)
        }

        // Extend the criteria by calling helper methods that set extra filters
        criteria {
            for (String filterName : filterMap.keySet()) {
                // Extra filters cannot be set when only one user was chosen
                if (filterMap.get(filterName) && (!oneUserChosen || filterName.equals('participant'))) {
                    extendCriteriaFor(filterName, delegate, filters)
                }
            }

            // Find out which ids to return
            projectionsForCriteria(delegate, template)
        } as List<Long[]>
    }

    /**
     * Extends the criteria with filters as set by the user
     * @param filterName The name of the filter used
     * @param criteria The criteria to extend
     * @param filters The values for the filters set by the user
     */
    private void extendCriteriaFor(String filterName, NamedCriteriaProxy criteria, GrailsParameterMap filters) {
        switch (filterName) {
            case 'participant':
                extendCriteriaForParticipant(criteria, filters)
                break
            case 'participantState':
                extendCriteriaForParticipantState(criteria, filters)
                break
            case 'paperState':
                extendCriteriaForPaperState(criteria, filters)
                break
            case 'eventDates':
                extendCriteriaForEventDates(criteria, filters)
                break
        }
    }

    /**
     * Extends the criteria with filters to create emails for a single participant
     * @param criteria The criteria to extend
     * @param filters The values for the filters set by the user
     */
    private void extendCriteriaForParticipant(NamedCriteriaProxy criteria, GrailsParameterMap filters) {
        if (filters.participant?.isLong()) {
            criteria.eq('id', filters.long('participant'))
        }
    }

    /**
     * Extends the criteria with filters to create emails for participants with the specified state
     * @param criteria The criteria to extend
     * @param filters The values for the filters set by the user
     */
    void extendCriteriaForParticipantState(NamedCriteriaProxy criteria, GrailsParameterMap filters) {
        if (filters.participantState?.isLong()) {
            criteria.participantDates {
                criteria.eq('state.id', filters.long('participantState'))
            }
        }
    }

    /**
     * Extends the criteria with filters to create emails for papers with the specified state
     * @param criteria The criteria to extend
     * @param filters The values for the filters set by the user
     */
    void extendCriteriaForPaperState(NamedCriteriaProxy criteria, GrailsParameterMap filters) {
        if (filters.paperState?.isLong()) {
            criteria.papers {
                criteria.eq('state.id', filters.long('paperState'))
            }
        }
    }

    /**
     * Extends the criteria with filters to create emails for participants for the specified event dates
     * @param criteria The criteria to extend
     * @param filters The values for the filters set by the user
     */
    private void extendCriteriaForEventDates(NamedCriteriaProxy criteria, GrailsParameterMap filters) {
        criteria.participantDates {
            if ((filters.eventDates instanceof String) && filters.eventDates?.isLong()) {
                criteria.eq('date.id', filters.eventDates.toLong())
            }
            else if (filters.eventDates) {
                criteria.'in'('date.id', filters.eventDates*.toLong())
            }
            // If nothing is selected, then nothing is done...
            else if (!filters.eventDates) {
                criteria.isNull('date.id')
            }
        }
    }

    /**
     * Indicates for the given criteria which ids should be returned
     * @param criteria The criteria in question
     * @param template The template, which indicates what ids should be returned
     */
    private void projectionsForCriteria(NamedCriteriaProxy criteria, EmailTemplate template) {
        criteria.projections {
            criteria.property('id')

            for (String association : template.getAssociationsNames()) {
                "$association" {
                    criteria.property('id')
                }
            }
        }
    }
}
