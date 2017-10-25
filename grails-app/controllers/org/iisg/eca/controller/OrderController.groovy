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
        if (message.containsKey('POST')) {
            if (message.isValid()) {
                boolean insert = false
                long orderId = new Long(message.get('orderid').toString())

                // Obtain the order (or create a new order) and refresh
                Order order = Order.get(orderId)
                if (!order) {
                    order = new Order()
                    order.setId(orderId)
                    insert = true
                }
                order.refreshOrder(insert)

                // If the payment is accepted and we know the participant, sent the payment accepted email
                if ((order.getPayed() == Order.PAYMENT_ACCEPTED) && order.participantDate) {
                    SentEmail email = emailCreationService.createPaymentAcceptedEmail(order.participantDate.user, order)
                    emailService.sendEmail(email)
                }
                else if (!order.participantDate) {
                    log.warn('Unknown participant for order ' + order.id)
                }

                render(text: 'OK')
                return
            }
        }
        response.sendError(400)
    }
}
