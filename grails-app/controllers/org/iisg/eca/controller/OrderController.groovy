package org.iisg.eca.controller

import org.codehaus.groovy.grails.web.util.WebUtils
import org.iisg.eca.domain.Order
import org.iisg.eca.domain.SentEmail
import org.iisg.eca.payway.PayWayMessage

/**
 * Controller responsible for handling order updates from PayWay
 */
class OrderController {
    def emailService
    def emailCreationService

    def post() {
        Map<String, Object> queryParams = WebUtils.fromQueryString(request.queryString)
        PayWayMessage message = new PayWayMessage(queryParams)
        if (message.containsKey('POST') && message.isValid()) {
            log.warn('Start 1')

            boolean insert = false
            long orderId = new Long(message.get('orderid').toString())

            log.warn('Start 2')

            // Obtain the order (or create a new order) and refresh
            Order order = Order.get(orderId)
            if (!order) {
                order = new Order()
                order.setId(orderId)
                insert = true
            }
            log.warn('Refresh order')
            order.refreshOrder(insert)

            // If the payment is accepted and we know the participant, sent the payment accepted email
            if ((order.getPayed() == Order.PAYMENT_ACCEPTED) && order.participantDate) {
                log.warn('Send mail!')
                SentEmail email = emailCreationService.createPaymentAcceptedEmail(order.participantDate.user, order)
                emailService.sendEmail(email)
            }
            else if (!order.participantDate) {
                log.warn('Unknown participant for order ' + order.id)
            }

            log.warn('OK!')
            render(text: 'OK')
            return
        }

        if (!message.containsKey('POST'))
            log.error('Received an post order with no key POST')
        if (!message.isValid())
            log.error('Received an invalid post order message')

        response.sendError(400)
    }
}
