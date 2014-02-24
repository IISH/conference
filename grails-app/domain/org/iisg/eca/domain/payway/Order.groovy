package org.iisg.eca.domain.payway

import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.ParticipantState
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.User
import org.iisg.eca.payway.PayWayMessage
import org.springframework.context.i18n.LocaleContextHolder

/**
 * The details of an order in PayWay
 */
class Order {
    def messageSource

    static final int ORDER_NOT_PAYED = 0
    static final int ORDER_PAYED = 1
    static final int ORDER_REFUND_OGONE = 2
	static final int ORDER_REFUND_BANK = 3

	static final int PAYMENT_ACCEPTED = 1
	static final int PAYMENT_DECLINED = 2
	static final int PAYMENT_EXCEPTION = 3
	static final int PAYMENT_CANCELLED = 4
	static final int PAYMENT_OTHER_STATUS = 5

    Long projectId
    String orderCode
    String userId
    String currency
    long amount = 0L
	long refundedAmount = 0L
    int payed = ORDER_NOT_PAYED
    boolean willPayByBank = false
    Date createdAt = new Date()
    Date updatedAt = new Date()
	Date refundedAt
    String description

    static mapping = {
        datasource 'payWay'
        table 'orders'
        version false

        id                  column: 'ID'
        projectId           column: 'project_id'
        orderCode           column: 'ordercode'
        userId              column: 'user_id'
        currency            column: 'currency'
        amount              column: 'amount'
	    refundedAmount      column: 'refunded_amount'
        payed               column: 'payed'
        willPayByBank       column: 'willpaybybank'
        createdAt           column: 'created_at'
        updatedAt           column: 'updated_at'
	    refundedAt          column: 'refunded_at'
        description         column: 'description',  type: 'text'
    }

    static constraints = {
        orderCode           maxSize: 50,    nullable: true
        userId              maxSize: 50,    nullable: true
        currency            maxSize: 10,    blank: false
        amount              min: 0L
	    refundedAmount      min: 0L
	    refundedAt                          nullable: true
        description                         blank: false
    }

    def beforeUpdate() {
        updatedAt = new Date()
    }

    BigDecimal getAmountAsBigDecimal() {
        new BigDecimal(amount).movePointLeft(2)
    }

	BigDecimal getRefundedAmountAsBigDecimal() {
		new BigDecimal(refundedAmount).movePointLeft(2)
	}

    User findUser() {
        User.get(userId.toLong())
    }

    /**
     * Load all orders of the given user in the PayWay database from last year
     * @param user The user to obtain orders from
     * @return A list of orders from the user from last year
     */
    static List<Order> findAllOrdersOfUserLastYear(User user) {
        Calendar calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -1)

        findAllOrdersOfUser(user, calendar.getTime())
    }

    /**
     * Load all orders of the given user in the PayWay database, starting from the given date
     * @param user The user to obtain orders from
     * @param minimumDate Obtain all orders created at least on this date
     * @return A list of orders from the user
     */
    static List<Order> findAllOrdersOfUser(User user, Date minimumDate) {
        Setting projectId = Setting.getSetting(Setting.PAYWAY_PROJECT_ID)

        if (projectId?.value) {
            Order.withCriteria {
                eq('projectId', projectId.value.toLong())
                eq('userId', user.id.toString())

                ge('createdAt', minimumDate)

                order('id', 'desc')
            }
        }
        else {
            return []
        }

    }

    /**
     * Get human readable text based on the status
     * @return A string indicating the status of this order
     */
    String getStatusText() {
        switch (payed) {
            case ORDER_PAYED:
                return messageSource.getMessage('order.status.payed.label', null, LocaleContextHolder.locale)
                break
            case ORDER_REFUND_OGONE:
                return messageSource.getMessage('order.status.refund.ogone.label', null, LocaleContextHolder.locale)
                break
	        case ORDER_REFUND_BANK:
		        return messageSource.getMessage('order.status.refund.bank.label', null, LocaleContextHolder.locale)
		        break
            case ORDER_NOT_PAYED:
            default:
                return messageSource.getMessage('order.status.not.payed.label', null, LocaleContextHolder.locale)
        }
    }

	/**
	 * Sets a bank transfer payed and active
	 * @param participant The participant who made the bank transfer
	 * @return Whether this order may be set payed and active
	 */
	boolean setPayedAndActive(ParticipantDate participant) {
		if (    (this.userId?.toLong() == participant.user.id) &&
				(this.payed == ORDER_NOT_PAYED) &&
				this.willPayByBank) {
			PayWayMessage message = new PayWayMessage()
			message.put('orderid', this.id)
			message.put('paymentresult', PAYMENT_ACCEPTED)
			PayWayMessage result = message.send('bankTransferPaymentResponse')

			if (result != null) {
				participant.paymentId = this.id
				if (participant.state.id == ParticipantState.REMOVED_CANCELLED) {
					participant.state = ParticipantState.get(ParticipantState.PARTICIPANT_DATA_CHECKED)
				}

				return true
			}
		}

		return false
	}

	/**
	 * Perform a full refund minus the administration costs
	 * @return Whether the refund was successful
	 */
	boolean fullRefund() {
		Long toRefund = 0L
		Setting refundCosts = Setting.getSetting(Setting.REFUND_ADMINISTRATION_COSTS)
		if (refundCosts?.value?.isLong()) {
			toRefund = this.amount - refundCosts.value.toLong()
		}
		long totalRefunded = this.refundedAmount + toRefund

		// Only if a payment has taken place and the total amount to refund does not exceed the payed amount the refund can continue
		if ((this.payed != ORDER_NOT_PAYED) && (totalRefunded <= this.amount) && (totalRefunded > 0L)) {
			PayWayMessage message = new PayWayMessage()
			message.put('orderid', this.id)
			message.put('amount', toRefund)
			PayWayMessage result = message.send('refundPayment')

			return (result != null)
		}

		return false
	}

    @Override
    String toString() {
        return "$orderCode: $amount $currency"
    }
}
