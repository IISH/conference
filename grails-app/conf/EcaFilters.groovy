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

import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientRegistrationException

/**
 * All filters for accessing a page
 */
class EcaFilters {
    def pageInformation
    def grailsApplication
    def gormClientDetailsService
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
                    return true
                }

                response.sendError(403)
                return false
            }
        }

        /**
         *  Every page (except login/logout/xhr) should be in the database, so lookup the page information from the database
         *  If it is there, cache the page information for this request
         */
        page(controller: '*', action: '*', controllerExclude: 'ajax|api|css|login|logout') {
            before = {
	            // Make sure old data is removed
	            pageInformation.removePage()

                if (!params.xhr) {
                    Page page = Page.findByControllerAndAction(params.controller.toString(), params.action.toString(), [cache: true])

                    if (page) {
                        pageInformation.page = page
                    }

                    // Also save the parameters of this page in a session
                    String sessionIdentifier = RandomStringUtils.random(8, true, true)
                    session.putValue(sessionIdentifier, params.clone())
                    pageInformation.sessionIdentifier = sessionIdentifier
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
				// Make sure old data is removed
	            pageInformation.removeDate()

                String eventCode = params.event?.replaceAll('-', ' ')
                String dateCode = params.date?.replaceAll('-', ' ')
                eventCode = eventCode ?: ""
                dateCode = dateCode ?: ""

                // Lookup the requested event and event date in the database
                Event event = Event.findByCode(eventCode, [cache: true])
                EventDate date = EventDate.findByEventAndYearCode(event, dateCode, [cache: true])

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
	            if ((params.controller == 'event') || (params.controller == 'css') || (params.controller == 'user') || ((params.controller == 'eventDate') && (params.action == 'create'))) {
	                return true
                }
	            else {
	                redirect(controller: 'event', action: 'list')
	                return false
                }
            }
            afterView = { Exception e ->
                pageInformation.removeDate()
            }
        }

        /**
         * The authorization filter
         */
        authFilter(controller: '*', action: '*', controllerExclude: 'login|logout|css|api|userApi') {
            before = {
                List<Role> rolesFullRights = Role.findAllByFullRights(true, [cache: true])
				List<Role> rolesOnlyLastDate = Role.findAllByOnlyLastDate(true, [cache: true])

                // No full rights? Then find out if the user has access
                if (!SpringSecurityUtils.ifAnyGranted(rolesFullRights*.role.join(','))) {
                    User user = User.get(springSecurityService.principal.id)

                    // First check on a tenant (event date) level
                    if (pageInformation.date?.event && (UserRole.findAllByUserAndEvent(user, pageInformation.date.event, [cache: true]).size() == 0)) {
                        // Unfortunately, no access to this event date specified in the database
                        response.sendError(403)
                        return false
                    }
                    // In case of a user with the role 'userLastDate', he/she only has access to the last date
	                else if (pageInformation.date && user.getRoles().find { rolesOnlyLastDate.contains(it) } && !pageInformation.date.isLastDate()) {
		                response.sendError(403)
		                return false
	                }
                    else if (   !pageInformation.date && params.id &&
                                ((params.controller == 'event') &&
                                (params.action == 'create' || params.action == 'list' || params.action == 'index'))) {
                        // Event and event date are tenants, but accessible from outside a tenant
                        // So check these specific actions, which need an event id, now
                        List<UserRole> access = UserRole.withCriteria {
                            cache(true)

                            eq('user.id', user.id)
                            eq('event.id', params.id)
                        }

                        // If the user has access to one of the event dates of the given event, it is ok
                        if (access.size() == 0) {
                            response.sendError(403)
                            return false
                        }
                    }

                    // Now check access on page level
                    if (pageInformation.page && !pageInformation.page.hasAccess()) {
                        response.sendError(403)
                        return false
                    }
                }

                // Everything is ok, allow access
                return true
            }
        }

        /**
         * The authorization filter for the API
         */
        authFilterApi(controller: 'api', action: '*') {
            before = {
                try {
                    String clientId
                    if (springSecurityService.principal instanceof String) {
                        clientId = springSecurityService.principal
                    }
                    else {
                        clientId = springSecurityService.principal?.username
                    }

                    ClientDetails client = gormClientDetailsService.loadClientByClientId(clientId)
                    if (client) {
                        String events = client.additionalInformation.get('events', '')
                        String[] eventCodes = events?.split(',')

                        // Find out if the client is allowed to request data from the requested event
                        if (eventCodes?.contains(pageInformation.date.event.code)) {
                            // Everything is ok, allow access
                            return true
                        }
                    }
                }
                catch (ClientRegistrationException gre) { }

                response.sendError(403)
                return false
            }
        }

         /**
          * Default filter for all requests
          */
        defaultFilter(controller: '*', action: '*') {
            before = {
                // Update locale of currently logged in user if it has changed
                if (params.lang && !(springSecurityService.principal instanceof String) && springSecurityService.principal?.class?.hasProperty('id')) {
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
                if (model != null) {
                    // Add the pageInformation bean information to all models by default
                    model.put('curPage', pageInformation.page)
                    model.put('curDate', pageInformation.date)

                    // Also add current language information to the model
	                model.put('curLocale', LocaleContextHolder.locale)
	                model.put('curLang', LocaleContextHolder.locale.language)
                }
            }
        }
    }
}
