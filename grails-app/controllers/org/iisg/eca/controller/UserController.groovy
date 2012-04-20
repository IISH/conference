package org.iisg.eca.controller

import javax.servlet.http.Cookie
import org.iisg.eca.domain.User

/**
 * Controller for user related actions
 */
class UserController {
    /**
	 * Dependency injection for the springSecurityService.
	 */
    def springSecurityService

    /**
     * All methods responding to HTTP POST calls
     */
    static allowedMethods = [update: "POST"]

    /**
     * Default action: Redirects to 'show' method, which will display information of the logged in user
     */
    def index() {
        redirect(action: "show", params: params)
    }

    /**
     * Displays information about the logged in user
     */
    def show() {
        [userInstance: User.get(springSecurityService.principal.id)]
    }

    /**
     * Shows the update form, allowing the logged in user to update his information
     */
    def edit() {
        [userInstance: User.get(springSecurityService.principal.id)]
    }

    /**
     * Validates all changes made by the user of his own information and saving it if validation succeeds
     */
    def update() {
        def userInstance = User.get(springSecurityService.principal.id)

        // For a password update, the new password has to be typed twice and has to match
        if (!params.password.equals(params.encryptedPasswordAgain)) {
            userInstance.errors.rejectValue 'password', message(code: 'user.password.nomatch')
            render(view: "edit", model: [userInstance: userInstance])
            return
        }

        // If the password field is empty, the password should not be updated
        // If that is the case, make sure not to overwrite the password with an empty String
        if (params.password?.isEmpty()) {
            params.password = userInstance.password
        }

        bindData(userInstance, params, [include: ['fullName', 'institute', 'language', 'password']])

        if (!userInstance.save(flush: true)) {
            render(view: "edit", model: [userInstance: userInstance])
            return
        }

        // Update the selected language
        def cookie = new Cookie('lang', userInstance.language)
        cookie.path = '/'
        response.addCookie(cookie)

        // Update the security session, to correctly reflect the updated information
        springSecurityService.reauthenticate userInstance.email

        flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label')])
        redirect(action: "show")
    }
}
