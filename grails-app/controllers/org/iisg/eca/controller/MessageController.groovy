package org.iisg.eca.controller

import grails.converters.JSON

class MessageController {
    /**
     * Returns localized messages called by client-side scripts
     */
    def index() {
        if (request.xhr) {
            def message = [message: message(code: params.code)]
            render message as JSON
        }
    }
}

