package org.iisg.eca.controller

import grails.converters.JSON
import org.iisg.eca.domain.User
import org.iisg.eca.utils.QueryTypeCriteriaBuilder

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
	 * PasswordService for password related actions
	 */
	def passwordService

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
                    'education', 'email', 'address', 'city', 'country', 'phone', 'mobile', 'cv',
                    'extraInfo', 'emailDiscontinued']], "User")

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
	        String oldPassword = params['User.password']
	        String newPassword = params['User.newPassword']
	        String newPasswordAgain = params['User.newPasswordAgain']

            // For a password update, the new password has to be typed twice and has to match
            if (!newPassword.equals(newPasswordAgain)) {
                user.errors.rejectValue('password', 'user.password.nomatch')
            }
            // If it is indeed a password update, make sure the old password is correct
            // and the new password is not empty
            else if (!newPassword.isEmpty() && user.isPasswordCorrect(oldPassword)) {
                if (passwordService.changePassword(user, newPassword, newPasswordAgain, oldPassword)) {
                    flash.message = g.message(code: 'default.updated.message', args:
	                        [g.message(code: 'user.newPassword.label'), user.toString()])
                }
            }
	        else {
	            // Incorrect password given
	            user.errors.rejectValue('password', 'user.password.incorrect')
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
		    String[] searchTerms = (params.terms) ? params.terms?.split() : []
		    String queryType = (params.query) ? params.query : 'allParticipants'

		    QueryTypeCriteriaBuilder queryTypeCriteriaBuilder = new QueryTypeCriteriaBuilder(pageInformation.date, queryType)
		    queryTypeCriteriaBuilder.setAdditionalCriteria {
		        projections {
			        distinct(['id', 'lastName', 'firstName', 'email'])
		        }

		        or {
			        for (String searchTerm : searchTerms) {
				        if (!searchTerm.isEmpty()) {
					        like('lastName', "%${searchTerm}%")
					        like('firstName', "%${searchTerm}%")
				        }
			        }
		        }

                maxResults(15)
		    }

		    List users = queryTypeCriteriaBuilder.getUniqueResults()
		    render users.collect { user ->
                [id: user[0], lastName: user[1], firstName: user[2], email: user[3]]
		    } as JSON
		}
    }

    /**
     * Show query types
     */
    def queryType() {
        if (!params.q)
            params.q = 'allParticipants'

        Set<String> queryTypes = QueryTypeCriteriaBuilder.getQueryTypes()
        if (params.q in queryTypes) {
            QueryTypeCriteriaBuilder queryTypeCriteriaBuilder = new QueryTypeCriteriaBuilder(pageInformation.date, params.q)
            queryTypeCriteriaBuilder.setAdditionalCriteria {
                projections {
                    distinct(['id', 'lastName', 'firstName'])
                }
            }
            List users = queryTypeCriteriaBuilder.getUniqueResults()

            render(view: 'queryType', model: [users: users, queryTypes: queryTypes])
            return
        }

        flash.error = true
        flash.message = g.message(code: 'default.not.found.message', args: [params.q])
        redirect(uri: eca.createLink(previous: true, noBase: true))
    }
}
