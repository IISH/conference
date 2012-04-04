package org.iisg.eca

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

class MessageController {
    /**
     * Returns localized messages called by client-side scripts
     */
    @Secured('permitAll')
    def index() {
        if (request.xhr) {
            def message = [message: message(code: params.code)]
            render message as JSON
        }
    }
}

