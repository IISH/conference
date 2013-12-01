import org.iisg.eca.domain.User
import org.iisg.eca.domain.Role
import org.iisg.eca.domain.Page
import org.iisg.eca.domain.Event
import org.iisg.eca.domain.UserRole
import org.iisg.eca.domain.EventDate
import org.iisg.eca.domain.EventDomain
import org.iisg.eca.domain.EventDateDomain
import org.iisg.eca.domain.IPAuthentication

import org.apache.commons.lang.RandomStringUtils
import org.springframework.context.i18n.LocaleContextHolder
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * All filters for accessing a page
 */
class EcaFilters {
    def pageInformation
    def grailsApplication
    def springSecurityService
    
    def filters = {

        /**
         * Perform IP authentication, is user allowed to access the application?
         */
        ipFilter(controller: '*', action: '*', controllerExclude: 'css') {
            before = {
                String clientIP = request.getHeader("X-Forwarded-For")?.trim()
                if (!clientIP || clientIP.isEmpty()) {
                    clientIP = request.getRemoteAddr()?.trim()
                }

                if (clientIP && IPAuthentication.isIPAllowed(clientIP)) {
                    return
                }

                response.sendError(403)
                return
            }
        }

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

                // Also save the parameters of this page in a session
                String sessionIdentifier = RandomStringUtils.random(8, true, true)
                session.putValue(sessionIdentifier, params.clone())
                pageInformation.sessionIdentifier = sessionIdentifier

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
                return ((params.controller == 'event') || (params.controller = 'css') || (params.controller == 'user') || ((params.controller == 'eventDate') && (params.action == 'create')))
            }
            afterView = { Exception e ->
                pageInformation.removeDate()
            }
        }

        /**
         * The authorization filter
         */
        authFilter(controller: '*', action: '*', controllerExclude: 'login|logout|css') {
            before = {
                List<Role> roles = Role.findAllByFullRights(true)

                // No full rights? Then find out if the user has access
                if (!SpringSecurityUtils.ifAnyGranted(roles*.role.join(','))) {
                    User user = User.get(springSecurityService.principal.id)

                    // First check on a tenant (event date) level
                    if (pageInformation.date?.event && (UserRole.findAllByUserAndEvent(user, pageInformation.date.event, [cache: true]).size() == 0)) {
                        // Unfortunately, no access to this event date specified in the database
                        response.sendError(403)
                        return
                    }
                    else if (   !pageInformation.date && params.id &&
                                ((params.controller == 'event') &&
                                (params.action == 'create' || params.action == 'list' || params.action == 'index'))) {
                        // Event and event date are tenants, but accessible from outside a tenant
                        // So check these specific actions, which need an event id, now
                        List<UserRole> access = UserRole.withCriteria {
                            eq('user.id', user.id)
                            eq('event.id', params.id)
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
                
                // Make sure IE does not cache AJAX responses
                if (request.xhr) {
                    response.setHeader('expires', '-1')
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

                // Also add current language information to the model
                model.put('curLang', LocaleContextHolder.locale.language)
            }
        }
    }
}
