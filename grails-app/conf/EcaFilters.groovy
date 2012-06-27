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
import org.iisg.eca.domain.UserRole

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
                        if (EventDateDomain.class.isAssignableFrom(domain)) {
                            domain.enableHibernateFilter('dateFilter').setParameter('dateId', date.id)
                        }
                        else if (EventDomain.class.isAssignableFrom(domain)) {
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
                List<Role> roles = Role.findAllByFullRights(true)

                // No full rights? Then find out if the user has access
                if (!SpringSecurityUtils.ifAnyGranted(roles*.role.join(','))) {
                    User user = User.get(springSecurityService.principal.id)

                    // First check on a tenant (event date) level
                    if (pageInformation.date && (UserRole.findAllByUserAndDate(user, pageInformation.date).size() == 0)) {
                        // Unfortunately, no access to this event date specified in the database
                        response.sendError(403)
                        return
                    }
                    else if (   !pageInformation.date && params.id &&
                                ((params.controller == 'event') && ((params.action == 'show') || (params.action == 'edit'))) ||
                                ((params.controller == 'eventDate') && (params.action == 'create'))) {
                        // Event and event date are tenants, but accessible from outside a tenant
                        // So check these specific actions, which need an event id, now
                        List<UserRole> access = UserRole.withCriteria {
                            eq('user.id', user.id)
                            inList('date.id', Event.get(params.id)?.dates*.id)
                        }

                        // If the user has access to one of the event dates of the given event, it is ok
                        if (access.size() == 0) {
                            response.sendError(403)
                            return
                        }
                    }

                    // Now check access on page level
                    if (pageInformation.page && !pageInformation.page.hasAccess()) {
                        response.sendError(403)
                        return
                    }
                }

                // Everything is ok, allow access
                return true
            }
        }
        
         /**
         * Default filter for all requests
         */
        defaultFilter(controller: '*', action: '*') {
            before = {
                // Update locale of currently logged in user if it has changed
                if (params.lang && !(springSecurityService.principal instanceof String)) {
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
