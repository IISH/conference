package org.iisg.eca.controller

import grails.converters.JSON
import org.iisg.eca.domain.User

/**
 * Controller responsible for handling default AJAX messages
 */
class AjaxController {

    /**
     * Returns localized messages called by client-side scripts
     * (AJAX call)
     */
    def message() {
        // Must be called via AJAX
        if (request.xhr && params.code) {
            // Look up the message and send it back in JSON format
            Map message = [success: true, message: g.message(code: params.code)]
            render message as JSON
        }
    }
    
    /**
     * Returns a JSON message indicating if the given email address is unique
     * (AJAX call)
     */
    def uniqueEmail() {
        // Must be called via AJAX
        if (request.xhr && params.email) {
            // Look up a user with this email address
            String email = params.email.trim()
            User user = User.findByEmail(email)
            
            Map message = [success: true]
            if (user) {
                String mergeLink = null
                if (params.user?.isLong()) {
                    User userOrg = User.findById(params.long('user'))
                    mergeLink = eca.link(
                            [controller    : 'participant',
                             action        : 'merge',
                             id            : userOrg.id,
                             params        : [user: user.id, back: params.back],
                             noPreviousInfo: true],
                            g.message(code: 'default.merge.message')
                    )
                }

                String msg = g.message(code: 'default.not.unique.email.message', args: [email])
                message = [success: false, message: msg, mergeLink: mergeLink]
            }
            
            render message as JSON
        }
    }
}

