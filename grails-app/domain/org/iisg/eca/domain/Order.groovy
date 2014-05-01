package org.iisg.eca.domain

import org.iisg.eca.payway.PayWayMessage
import org.springframework.context.i18n.LocaleContextHolder

/**
 * The details of an order in PayWay
 */
class Order {
	def messageSource
	def pageInformation

	static final int ORDER_NOT_PAYED = 0
	static final int ORDER_PAYED = 1
	static final int ORDER_REFUND_OGONE = 2
	static final int ORDER_REFUND_BANK = 3

	static final int PAYMENT_ACCEPTED = 1
	static final int PAYMENT_DECLINED = 2
	static final int PAYMENT_EXCEPTION = 3
	static final int PAYMENT_CANCELLED = 4
	static final int PAYMENT_OTHER_STATUS = 5

	String orderCode
	ParticipantDate participantDate
	long amount = 0L
	long refundedAmount = 0L
	int payed = ORDER_NOT_PAYED
	boolean willPayByBank = false
	Date createdAt = new Date()
	Date updatedAt = new Date()
	Date refundedAt
	String description

	static belongsTo = ParticipantDate

	static mapping = {
		table 'orders'
		version false
		sort id: 'desc'

		id              column: 'order_id',     generator: 'assigned'
		orderCode       column: 'ordercode'
		participantDate column: 'participant_date_id'
		amount          column: 'amount'
		refundedAmount  column: 'refunded_amount'
		payed           column: 'payed'
		willPayByBank   column: 'willpaybybank'
		createdAt       column: 'created_at'
		updatedAt       column: 'updated_at'
		refundedAt      column: 'refunded_at'
		description     column: 'description',  type: 'text'
	}

	static constraints = {
		orderCode       maxSize: 50,    nullable: true
		amount          min: 0L
		refundedAmount  min: 0L
		refundedAt                      nullable: true
		description                     blank: false
	}

	BigDecimal getAmountAsBigDecimal() {
		new BigDecimal(amount).movePointLeft(2)
	}

	BigDecimal getRefundedAmountAsBigDecimal() {
		new BigDecimal(refundedAmount).movePointLeft(2)
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
		if ((this.participantDate.id == participant.id) && (this.payed == ORDER_NOT_PAYED) && this.willPayByBank) {
			PayWayMessage message = new PayWayMessage()
			message.put('orderid', this.id)
			message.put('paymentresult', PAYMENT_ACCEPTED)
			PayWayMessage result = message.send('bankTransferPaymentResponse')

			if (result != null) {
				participant.paymentId = this.id
				if (participant.state.id == ParticipantState.REMOVED_CANCELLED) {
					participant.state = ParticipantState.get(ParticipantState.PARTICIPANT_DATA_CHECKED)
				}

				return refreshOrder()
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

			if (result != null) {
				return refreshOrder()
			}
		}

		return false
	}

	/**
	 * Refreshes the order by requesting the order details from PayWay
	 * @param insert Whether this is an insert, or else an update
	 * @return Whether the refresh was successful or not
	 */
	boolean refreshOrder(boolean insert = false) {
		PayWayMessage message = new PayWayMessage()
		message.put('orderid', this.id)
		PayWayMessage result = message.send('orderDetails')

		if (result != null) {
			this.orderCode = result.get('ORDERCODE')
			this.amount = new Long(result.get('AMOUNT').toString())
			this.refundedAmount = (result.get('REFUNDEDAMOUNT')?.isLong()) ? new Long(result.get('REFUNDEDAMOUNT').toString()) : 0L
			this.payed = new Integer(result.get('PAYED').toString())
			this.willPayByBank = result.get('WILLPAYBYBANK')
			this.createdAt = (Date) result.get('CREATEDAT', true)
			this.updatedAt = (Date) result.get('UPDATEDAT', true)
			this.refundedAt =  (result.get('REFUNDEDAT')) ? (Date) result.get('REFUNDEDAT', true) : null
			this.description = result.get('COM')

			EventDate date = pageInformation.date
			Long userId = result.get('USERID')?.toString()?.isLong() ? new Long(result.get('USERID').toString()) : null
			if (date && userId) {
				User user = User.get(userId)
				ParticipantDate participant = ParticipantDate.findByUserAndDate(user, date)
				if (participant) {
					this.participantDate = participant
				}
			}

			return this.save(insert: insert)
		}

		return false
	}

	/**
	 * Creates an extended order description describing the final registration of the participant
	 * @return An extended order description
	 */
	String getExtendedOrderDescription() {
		List<String> orders = new ArrayList<String>()

		int nrDays = ParticipantDay.findAllDaysOfUser(participantDate.user).size()
		FeeAmount feeAmount = FeeAmount.getFeeAmountForDateAndNrDays(participantDate.feeState, nrDays, createdAt).get()
		orders.add("- $feeAmount")

		participantDate.extras.each {
			if (it.amount > 0) {
				orders.add("- ${it.title}: ${FeeAmount.getReadableFeeAmount(it.amount)}")
			}
			else {
				orders.add("- $it.title")
			}
		}

		Setting showAccompanyingPersons = Setting.getSetting(Setting.SHOW_ACCOMPANYING_PERSONS)
		if (showAccompanyingPersons?.booleanValue) {
			FeeState accompanyingPersonFeeSate = FeeState.getAccompanyingPersonFee()
			FeeAmount accompanyingPersonFeeAmount = FeeAmount.
					getFeeAmountForDateAndNrDays(accompanyingPersonFeeSate, nrDays, createdAt).get()

			participantDate.accompanyingPersons.each { String accompanyingPerson ->
				String readableFeeAmount = FeeAmount.getReadableFeeAmount(accompanyingPersonFeeAmount.feeAmount)
				orders.add("- ${accompanyingPerson}: $readableFeeAmount")
			}
		}

		orders.join('\n')
	}

	@Override
	String toString() {
		return "$orderCode: $amount"
	}
}
