package org.iisg.eca.security

import groovy.transform.CompileStatic

import javax.servlet.http.HttpServletRequest
import grails.plugin.springsecurity.web.authentication.GrailsUsernamePasswordAuthenticationFilter

/**
 * Override the GrailsUsernamePasswordAuthenticationFilter to make sure the entered password is always trimmed
 */
@CompileStatic
class MyUsernamePasswordAuthenticationFilter extends GrailsUsernamePasswordAuthenticationFilter {
	@Override
	protected String obtainPassword(HttpServletRequest request) {
		String password = super.obtainPassword(request)
		return password?.trim()
	}
}
