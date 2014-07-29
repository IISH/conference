package org.iisg.eca.security

import javax.servlet.http.HttpServletRequest
import grails.plugin.springsecurity.web.authentication.RequestHolderAuthenticationFilter

/**
 * Override the RequestHolderAuthenticationFilter to make sure the entered password is always trimmed
 */
class MyRequestHolderAuthenticationFilter extends RequestHolderAuthenticationFilter {
	@Override
	protected String obtainPassword(HttpServletRequest request) {
		String password = super.obtainPassword(request)
		return password?.trim()
	}
}
