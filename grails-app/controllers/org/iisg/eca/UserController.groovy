package org.iisg.eca

import javax.servlet.http.Cookie

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
        if (!params.encryptedPassword.equals(params.encryptedPasswordAgain)) {
            userInstance.errors.rejectValue 'encryptedPassword', message(code: 'user.password.nomatch')
            render(view: "edit", model: [userInstance: userInstance])
            return
        }

        // If the password field is empty, the password should not be updated
        // If that is the case, make sure not to overwrite the password with an empty String
        if (params.encryptedPassword?.isEmpty()) {
            params.encryptedPassword = userInstance.encryptedPassword
        }

        bindData(userInstance, params, [include: ['fullName', 'institute', 'language', 'encryptedPassword']])

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
