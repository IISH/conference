package org.iisg.eca.controller

import org.iisg.eca.domain.User
import org.iisg.eca.domain.EmailCode
import org.iisg.eca.domain.EmailTemplate
import org.iisg.eca.domain.ParticipantDate

class EmailController {
    def pageInformation

    def index() {
        redirect(action: 'list', params: params)
    }

    def list() { }

    def send() {
        if (!params.id) {
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        EmailTemplate emailTemplate = EmailTemplate.findById(params.id)
        if (!emailTemplate) {
            flash.message =  g.message(code: 'default.not.found.message', args: [message(code: 'email.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        if (request.get) {
            render view: "filter_${params.type}", model: [emailTemplate: emailTemplate, participants: ParticipantDate.createCriteria().list {}]
        }
        else if (request.post) {
            List<ParticipantDate> participants
            def criteria

            if (emailTemplate.queryType) {
                criteria = ParticipantDate."${emailTemplate.queryType}"(pageInformation.date)
            }
            else {
                criteria = ParticipantDate.createCriteria()
            }

            participants = criteria {
                if (params.state && params.state != "null") {
                    eq('state.id', params.long('state'))
                }
                if (params.participant && params.participant != "null") {
                    eq('id', params.long('participant'))
                }

                user {
                    papers {
                        if (params.paper) {
                            eq('state.id', params.long('paper'))
                        }
                    }
                }
            }

            // TODO: Send emails

            flash.message = "Email send to ${participants.size()} participants"
            redirect(uri: eca.createLink(previous: true, noBase: true))
        }
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
