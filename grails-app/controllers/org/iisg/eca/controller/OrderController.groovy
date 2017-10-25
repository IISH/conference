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
        log.error('aaa1')

        Map<String, Object> queryParams = WebUtils.fromQueryString(request.queryString)
        PayWayMessage message = new PayWayMessage(queryParams)

        log.error('aaa2')

        if (message.containsKey('POST')) {

            log.error('aaa3')

            if (message.isValid()) {

                log.error('aaa4')

                boolean insert = false // is it a new order
                long orderId = new Long(message.get('orderid').toString())
                log.error ('orderId: ' + orderId )

                // Obtain the order (or create a new order) and refresh
                Order order = Order.get(orderId)
                if (!order) {
                    log.error('new Order()')
                    order = new Order()
                    order.setId(orderId)
                    insert = true // is a new order
                }
                order.refreshOrder(insert)

                log.error ('orderId (2): ' + orderId )

                // If the payment is accepted and we know the participant, sent the payment accepted email
                log.error('order.getPayed(): ' + order.getPayed())
                log.error('order.participantDate: ' + order.participantDate)
                if ( (order.getPayed() == Order.PAYMENT_ACCEPTED) && order.participantDate ) {
                    log.error('aaa5a');

                    SentEmail email = emailCreationService.createPaymentAcceptedEmail(order.participantDate.user, order)
                    emailService.sendEmail(email)
                }
                else {
                    log.error('aaa5b');
                    if (!order.participantDate) {
                        log.error('aaa5b1');
                        log.warn('Unknown participant for order ' + order.id)
                    } else {
                        log.error('aaa5b2');
                    }
                }

                render(text: 'OK')
                return
            }
        }

        log.error('aaaError');
        response.sendError(400)
    }
}
