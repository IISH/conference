package org.iisg.eca.service

import org.iisg.eca.domain.EmailTemplate
import org.iisg.eca.domain.EventDate
import org.iisg.eca.utils.PageInformation
import org.iisg.eca.utils.QueryTypeCriteriaBuilder

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

        // If only one user is chosen, different rules count
        boolean oneUserChosen = (filterMap['participant'] && filters.participant?.isLong())
        String queryType = 'allParticipants'

        // The email template chosen could contain a query type (a named query to call)
        if (!oneUserChosen && template.queryTypeMultiple) {
            queryType = template.queryTypeMultiple
        }
        else if (oneUserChosen && template.queryTypeOne) {
            queryType = template.queryTypeOne
        }

        QueryTypeCriteriaBuilder queryTypeCriteriaBuilder = new QueryTypeCriteriaBuilder(pageInformation.date, queryType)
        queryTypeCriteriaBuilder.setAdditionalCriteria {
            // Extend the criteria by calling helper methods that set extra filters
            for (String filterName : filterMap.keySet()) {
                // Extra filters cannot be set when only one user was chosen
                if (filterMap.get(filterName) && (!oneUserChosen || filterName.equals('participant'))) {
                    extendCriteriaFor(filterName, delegate, filters)
                }
            }

            // Find out which ids to return
            projectionsForCriteria(delegate, template)
        }

        return queryTypeCriteriaBuilder.getUniqueResults() as List<Long[]>
    }

    /**
     * Extends the criteria with filters as set by the user
     * @param filterName The name of the filter used
     * @param criteria The criteria to extend
     * @param filters The values for the filters set by the user
     */
    private static void extendCriteriaFor(String filterName, NamedCriteriaProxy criteria, GrailsParameterMap filters) {
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
            case 'extras':
                extendCriteriaForExtras(criteria, filters)
                break
        }
    }

    /**
     * Extends the criteria with filters to create emails for a single participant
     * @param criteria The criteria to extend
     * @param filters The values for the filters set by the user
     */
    private static void extendCriteriaForParticipant(NamedCriteriaProxy criteria, GrailsParameterMap filters) {
        if (filters.participant?.isLong()) {
            criteria.eq('id', filters.long('participant'))
        }
    }

    /**
     * Extends the criteria with filters to create emails for participants with the specified state
     * @param criteria The criteria to extend
     * @param filters The values for the filters set by the user
     */
    private static void extendCriteriaForParticipantState(NamedCriteriaProxy criteria, GrailsParameterMap filters) {
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
    private static void extendCriteriaForPaperState(NamedCriteriaProxy criteria, GrailsParameterMap filters) {
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
    private static void extendCriteriaForEventDates(NamedCriteriaProxy criteria, GrailsParameterMap filters) {
        criteria.participantDates {
            if (filters.eventDates?.isLong()) {
                EventDate date = EventDate.get(filters.long('eventDates'))
                List<EventDate> eventDates = EventDate.getDateAndLaterDates(date).list()
                criteria.'in'('date.id', eventDates*.id)
            }
        }
    }

    /**
     * Extends the criteria with filters to create emails for participants with the specified extra
     * @param criteria The criteria to extend
     * @param filters The values for the filters set by the user
     */
    private static void extendCriteriaForExtras(NamedCriteriaProxy criteria, GrailsParameterMap filters) {
        if (filters.extra?.isLong()) {
            criteria.participantDates {
                criteria.extras {
                    criteria.eq('id', filters.long('extra'))
                }
            }
        }
    }

    /**
     * Indicates for the given criteria which ids should be returned
     * @param criteria The criteria in question
     * @param template The template, which indicates what ids should be returned
     */
    private static void projectionsForCriteria(NamedCriteriaProxy criteria, EmailTemplate template) {
        criteria.projections {
            if (template.getAssociationsNames().size() == 0) {
                criteria.distinct('id')
            }
            else {
                criteria.property('id')
            }

            for (String association : template.getAssociationsNames()) {
                if (association.contains('.')) {
                    String[] associations = association.split('\\.')
                    "${associations[0]}" {
                        criteria.property("${associations[1]}.id")
                    }
                }
                else {
                    criteria.property("${association}.id")
                }
            }
        }
    }
}
