package org.iisg.eca.controller

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Controller for logout actions
 */
class LogoutController {
 	/**
	 * Index action. Redirects to the Spring security logout uri.
	 */
	def index() {
		redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl // '/j_spring_security_logout'
	}
}
