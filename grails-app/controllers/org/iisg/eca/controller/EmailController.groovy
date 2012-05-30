package org.iisg.eca.controller

import org.iisg.eca.domain.User
import org.iisg.eca.domain.EmailCode
import org.iisg.eca.domain.EmailTemplate

class EmailController {
    def index() {
        redirect(action: 'list', params: params)
    }

    def list() {

    }

    def send() {

    }

    def example() {
        EmailTemplate template = EmailTemplate.findById(params.id)
        User user = User.get(params.user)

        String email = template.body.toString()
        for (EmailCode code : EmailCode.list()) {
            if (email.contains("[${code.code}]")) {
                email = email.replaceAll("\\[${code.code}\\]", code.translateForUser(user))
            }
        }

        [email: email]
    }
}
