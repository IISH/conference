package org.iisg.eca.controller
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import org.iisg.eca.domain.User
import org.iisg.eca.payway.PayWayMessage
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.web.WebAttributes

import javax.servlet.http.HttpServletResponse
/**
 * Controller for login related actions
 */
class LoginController {

	/**
	 * Dependency injection for the authenticationTrustResolver.
	 */
	def authenticationTrustResolver

	/**
	 * Dependency injection for the springSecurityService.
	 */
	def springSecurityService

    /**
     * EmailService for mailing the new password when requested
     */
    def emailService

	/**
	 * PasswordService for password related actions
	 */
	def passwordService

	/**
	 * Default action; redirects to 'defaultTargetUrl' if logged in, /login/auth otherwise.
	 */
	def index() {
		if (springSecurityService.isLoggedIn()) {
			redirect uri: SpringSecurityUtils.securityConfig.successHandler.defaultTargetUrl
		}
		else {
			redirect action: 'auth', params: params
		}
	}

	/**
	 * Show the login page.
	 */
	def auth() {
		def config = SpringSecurityUtils.securityConfig

		if (springSecurityService.isLoggedIn()) {
			redirect uri: config.successHandler.defaultTargetUrl
			return
		}

		String view = 'auth'
		String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"
		render view: view, model: [postUrl: postUrl,
		                           rememberMeParameter: config.rememberMe.parameter]
	}

	/**
	 * The redirect action for Ajax requests.
	 */
	def authAjax() {
		response.setHeader 'Location', SpringSecurityUtils.securityConfig.auth.ajaxLoginFormUrl
		response.sendError HttpServletResponse.SC_UNAUTHORIZED
	}

	/**
	 * Show denied page.
	 */
	def denied() {
		if (springSecurityService.isLoggedIn() &&
				authenticationTrustResolver.isRememberMe(SCH.context?.authentication)) {
			// have cookie but the page is guarded with IS_AUTHENTICATED_FULLY
			redirect action: 'full', params: params
		}
	}

	/**
	 * Login page for users with a remember-me cookie but accessing a IS_AUTHENTICATED_FULLY page.
	 */
	def full() {
		def config = SpringSecurityUtils.securityConfig
		render view: 'auth', params: params,
			model: [hasCookie: authenticationTrustResolver.isRememberMe(SCH.context?.authentication),
			        postUrl: "${request.contextPath}${config.apf.filterProcessesUrl}"]
	}

	/**
	 * Callback after a failed login. Redirects to the auth page with a warning message.
	 */
	def authfail() {
		String msg = ''
		def exception = session[WebAttributes.AUTHENTICATION_EXCEPTION]
		if (exception) {
			if (exception instanceof AccountExpiredException) {
				msg = g.message(code: "springSecurity.errors.login.expired")
			}
			else if (exception instanceof CredentialsExpiredException) {
				msg = g.message(code: "springSecurity.errors.login.passwordExpired")
			}
			else if (exception instanceof DisabledException) {
				msg = g.message(code: "springSecurity.errors.login.disabled")
			}
			else if (exception instanceof LockedException) {
				msg = g.message(code: "springSecurity.errors.login.locked")
			}
			else {
				msg = g.message(code: "springSecurity.errors.login.fail")
			}
		}

		if (springSecurityService.isAjax(request)) {
			render([error: msg] as JSON)
		}
		else {
            flash.error = true
			flash.message = msg
			redirect action: 'auth', params: params
		}
	}

	/**
	 * The Ajax success redirect url.
	 */
	def ajaxSuccess() {
		render([success: true, username: springSecurityService.authentication.name] as JSON)
	}

	/**
	 * The Ajax denied redirect url.
	 */
	def ajaxDenied() {
		render([error: 'access denied'] as JSON)
	}

    /**
     * The call to the forgot password page
     */
    def forgotPassword() {
        def config = SpringSecurityUtils.securityConfig

        if (springSecurityService.isLoggedIn()) {
			redirect uri: config.successHandler.defaultTargetUrl
			return
		}

        render view: 'forgot'
    }

    /**
     * Gives the user a code to request a new password
     */
    def newPassword() {
        // Find the user and create a new request code
        User user = User.findByEmail(params.j_username)

        // We need a user to give him/her a new request code
        if (!user) {
            flash.error = true
            flash.message = g.message(code: 'springSecurity.forgot.fail')
            render view: 'forgot'
            return
        }

        // Try to request a new request code for the user
        if (!passwordService.lostPassword(user)) {
            flash.error = true
            flash.message = g.message(code: 'springSecurity.forgot.error')
            render view: 'forgot'
            return
        }

        // Redirect to the login page with a success message
        flash.message = g.message(code: 'springSecurity.forgot.code.request')
        redirect action: 'auth'
    }

    /**
     * Gives the user a new password, if the request code is correct
     */
    def checkRequestCode() {
        User user = User.get(params.id)

        // We need a user to give him/her a new password
        if (!user) {
            flash.error = true
            flash.message = g.message(code: 'springSecurity.forgot.fail')
            render view: 'auth'
            return
        }

	    // Try to confirm the given request code by the user in order to send him/her a new password
	    switch (passwordService.confirmLostPassword(user, params.code.toString())) {
		    case passwordService.CONFIRM_LOST_PASSWORD_ACCEPT:
			    flash.message = g.message(code: 'springSecurity.forgot.success')
			    break
		    case passwordService.CONFIRM_LOST_PASSWORD_CODE_EXPIRED:
			    flash.error = true
			    flash.message = g.message(code: 'springSecurity.forgot.code.expired')
			    break
		    case passwordService.CONFIRM_LOST_PASSWORD_PASSWORD_ALREADY_SENT:
			    flash.error = true
			    flash.message = g.message(code: 'springSecurity.forgot.password.sent')
			    break
		    case passwordService.CONFIRM_LOST_PASSWORD_NOT_FOUND:
		    default:
			    flash.error = true
			    flash.message = g.message(code: 'springSecurity.forgot.code.incorrect')
	    }

	    redirect action: 'auth'
    }
}
