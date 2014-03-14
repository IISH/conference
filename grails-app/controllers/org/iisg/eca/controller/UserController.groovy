package org.iisg.eca.controller

import org.iisg.eca.domain.User
import grails.converters.JSON

/**
 * Controller for user related actions
 */
class UserController {
    /**
	 * Dependency injection for the springSecurityService.
	 */
    def springSecurityService

    /**
     * Information about the current page
     */
    def pageInformation

    /**
     * Default action: Redirects to 'show' method, which will display information of the logged in user
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
    }

    /**
     * Displays information about the logged in user
     */
    def show() {
        [user: User.get(springSecurityService.principal.id)]
    }

    /**
     * Shows the update form, allowing the logged in user to update his information
     */
    def edit() {
        User user = User.get(springSecurityService.principal.id)

        if (request.get) {
            [user: user]
        }
        else if (request.post) {
            bindData(user, params, [include: ['title', 'firstName', 'lastName', 'gender', 'organisation', 'department',
                    'email', 'address', 'city', 'country', 'phone', 'mobile', 'cv', 'extraInfo', 'emailDiscontinued']], "User")

            if (!user.save(flush: true)) {
                render(view: "edit", model: [user: user])
                return
            }

            // Update the security session, to correctly reflect the updated information
            springSecurityService.reauthenticate user.email

            flash.message = g.message(code: 'default.updated.message', args: [g.message(code: 'user.label'), user.toString()])
            if (params['btn_save_close']) {
                redirect(uri: eca.createLink(previous: true, noBase: true))
            }
            else {
                render(view: "edit", model: [user: user])
            }
        }
    }

    /**
     * Allows the user to change his/her password
     */
    def changePassword() {
        User user = User.get(springSecurityService.principal.id)

        if (request.post) {
            // For a password update, the new password has to be typed twice and has to match
            if (!params['User.newPassword'].equals(params['User.newPasswordAgain'])) {
                user.errors.rejectValue 'password', g.message(code: 'user.password.nomatch')
            }
            // If it is indeed a password update, make sure the old password is correct
            // and the new password is not empty
            else if (!params['User.newPassword'].isEmpty()) {
                if (user.isPasswordCorrect(params['User.password'])) {
                    user.password = params['User.newPassword']
                    if (user.save(flush: true)) {
                        flash.message = g.message(code: 'default.updated.message', args: [g.message(code: 'user.newPassword.label'), user.toString()])
                    }
                }
                else {
                    // Incorrect password given
                    user.errors.rejectValue 'password', g.message(code: 'user.password.incorrect')
                }
            }
        }

        render(view: "changePassword", model: [user: user])
    }

    /**
     * Returns a list will all users for auto complete boxes
     * (AJAX call)
     */
    def usersAutoComplete() {
       // If this is an AJAX call, continue
       if (request.xhr) {
           // By default, only return users that are a participant in the current event date
           def criteria = User.allParticipants(pageInformation.date)

           if (params.query) {
               criteria = User."$params.query"(pageInformation.date)
           }

           String[] searchTerms = params.terms.toString().split()
           def users = criteria {
               projections {
                   distinct(['id', 'lastName', 'firstName'])
               }

               or {
                   for (String searchTerm : searchTerms) {
                       if (!searchTerm.isEmpty()) {
                           like('lastName', "%${searchTerm}%")
                           like('firstName', "%${searchTerm}%")
                       }
                   }
               }
           }

           render users.collect { user ->
               [label: "${user[1]}, ${user[2]}", value: user[0]]
           } as JSON
       }
    }
}
