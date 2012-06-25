import org.iisg.eca.domain.User
import org.iisg.eca.domain.Page
import org.iisg.eca.domain.Event
import org.iisg.eca.domain.UserPage
import org.iisg.eca.domain.EventDate
import org.iisg.eca.domain.EventDomain
import org.iisg.eca.domain.EventDateDomain

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.iisg.eca.domain.Group
import org.iisg.eca.domain.Role

/**
 * All filters for accessing a page
 */
class EcaFilters {
    def pageInformation
    def grailsApplication
    def springSecurityService
    
    def filters = {

        /**
         *  Every page (except login/logout/xhr) should be in the database, so lookup the page information from the database
         *  If it is there, cache the page information for this request
         */
        page(controller: '*', action: '*') {
            before = {
                Page page = Page.findByControllerAndAction(params.controller, params.action)

                if (page) {
                    pageInformation.page = page
                }

                return true
            }
            afterView = { Exception e ->
                pageInformation.removePage()
            }
        }

        /**
         *  Every page (except login/logout/user/event related actions) should belong to a certain event
         *  Lookup the event and event date for this request in the database
         *  If it is there, allow access and cache the event date information for this request
         */
        eventDate(controller: '*', action: '*', controllerExclude: 'login|logout') {
            before = {
                String eventCode = params.event?.replaceAll('-', ' ')
                String dateCode = params.date?.replaceAll('-', ' ')
                eventCode = eventCode ?: ""
                dateCode = dateCode ?: ""

                // Lookup the requested event and event date in the database
                Event event = Event.findByCode(eventCode)
                EventDate date = EventDate.findByEventAndYearCode(event, dateCode)

                if (date) {
                    pageInformation.date = date

                    // Enable Hibernate filters on the domain classes for this event date
                    grailsApplication.domainClasses.each { domainClass -> 
                        Class domain = domainClass.clazz
                        if ((domainClass.name == "EventDate") || EventDateDomain.class.isAssignableFrom(domain)) {
                            domain.enableHibernateFilter('dateFilter').setParameter('dateId', date.id)
                        }
                        else if ((domainClass.name == "Event") || EventDomain.class.isAssignableFrom(domain)) {
                            domain.enableHibernateFilter('eventFilter').setParameter('eventId', date.event.id)  
                        }
                    }
                    
                    return true
                }

                // Event actions and user actions may be performed from outside a tenant
                // Just like creating a new event date
                return ((params.controller == 'event') || (params.controller == 'user') || ((params.controller == 'eventDate') && (params.action == 'create')))
            }
            afterView = { Exception e ->
                pageInformation.removeDate()
            }
        }

        /**
         * The authorization filter
         */
        authFilter(controller: '*', action: '*', controllerExclude: 'login|logout') {
            before = {
                def roles = Role.findAllByFullRights(true)

                // Check access on the event, if the user is not granted access to all events
                // and is not granted access to this specific event, deny access
                if (    pageInformation.date &&
                        !SpringSecurityUtils.ifAnyGranted(roles*.role.join(',')) &&
                        !UserPage.findAllByUserAndDate(User.get(springSecurityService.principal.id), pageInformation.date)) {
                    response.sendError(403)
                    return
                }

                // Now check access on page level
                if (!pageInformation.page) {
                    return true
                }
                else if (pageInformation.page.hasAccess()) {
                    return true
                }
                else {
                    response.sendError(403)
                    return
                }
            }
        }
        
         /**
         * Default filter for all requests
         */
        defaultFilter(controller: '*', action: '*') {
            before = {
                // Update locale of currently logged in user if it has changed
                if (params.lang) {
                    User user = User.get(springSecurityService.principal.id)
                    if (user) {
                        user.language = params.lang
                        user.save()
                    }
                }

                return true
            }
            after = { Map model ->
                if (!model) {
                    model = [:]
                }
                
                // Add the pageInformation bean information to all models by default
                model.put('curPage', pageInformation.page)
                model.put('curDate', pageInformation.date)
            }
        }
    }
}
