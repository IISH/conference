package org.iisg.eca.controller

import org.iisg.eca.jobs.CreateEmailJob

import org.iisg.eca.domain.EmailTemplate
import org.iisg.eca.domain.ParticipantDate

class EmailController {
    def pageInformation

    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
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

        if (request.post) {
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
                if (params.paper && params.paper != "null") {
                    user {
                        papers {
                            eq('state.id', params.long('paper'))
                        }
                    }
                }
            }

            CreateEmailJob.triggerNow([participants: participants, template: emailTemplate])

            flash.message = g.message(code: 'email.background.message', attrs: "${[participants.size()]}")
        }

        if (!params.type) {
            params.type = "default"
        }

        render view: "filter_${params.type}", model: [emailTemplate: emailTemplate, participants: ParticipantDate.createCriteria().list {}]
    }
}
