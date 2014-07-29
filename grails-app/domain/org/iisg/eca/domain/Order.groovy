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

	static final int ORDER_OGONE_PAYMENT = 0
	static final int ORDER_BANK_PAYMENT = 1
	static final int ORDER_CASH_PAYMENT = 2

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
	int paymentMethod = ORDER_OGONE_PAYMENT
	Date createdAt = new Date()
	Date updatedAtPayWay = new Date()
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
		paymentMethod   column: 'payment_method'
		createdAt       column: 'created_at'
		updatedAtPayWay column: 'updated_at'
		refundedAt      column: 'refunded_at'
		description     column: 'description'
	}

	static constraints = {
		orderCode       maxSize: 50,    nullable: true
		amount          min: 0L
		refundedAmount  min: 0L
		refundedAt                      nullable: true
		description     maxSize: 100,   blank: false
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
	 * Get human readable text based on the payment method
	 * @return A string indicating the payment method of this order
	 */
	String getPaymentMethodText() {
		switch (paymentMethod) {
			case ORDER_BANK_PAYMENT:
				return messageSource.getMessage('order.method.bank.label', null, LocaleContextHolder.locale)
				break
			case ORDER_CASH_PAYMENT:
				return messageSource.getMessage('order.method.cash.label', null, LocaleContextHolder.locale)
				break
			case ORDER_OGONE_PAYMENT:
			default:
				return messageSource.getMessage('order.method.ogone.label', null, LocaleContextHolder.locale)
		}
	}

	/**
	 * Sets a bank transfer or cash payment payed and active
	 * @param participant The participant who made the bank transfer or cash payment
	 * @return Whether this order may be set payed and active
	 */
	boolean setPayedAndActive(ParticipantDate participant) {
		if (    (this.participantDate.id == participant.id) &&
				(this.payed == ORDER_NOT_PAYED) &&
				(this.paymentMethod != ORDER_OGONE_PAYMENT)) {
			PayWayMessage message = new PayWayMessage()
			message.put('orderid', this.id)
			message.put('paymentresult', PAYMENT_ACCEPTED)
			PayWayMessage result = message.send('nonOgonePaymentResponse')

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
			this.paymentMethod = new Integer(result.get('PAYMENTMETHOD').toString())
			this.createdAt = (Date) result.get('CREATEDAT', true)
			this.updatedAtPayWay = (Date) result.get('UPDATEDAT', true)
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

			return this.save(insert: insert, flush: true)
		}

		return false
	}

	/**
	 * New orders should also be registered in PayWay, which this method takes care of
	 * @return Whether the action was successful or not
	 */
	boolean registerInPayWay() {
		PayWayMessage message = new PayWayMessage([
				"AMOUNT": this.amount,
				"CURRENCY": 'EUR',
				"LANGUAGE": 'en_US',
				"CN": this.participantDate.user.fullName,
				"EMAIL": this.participantDate.user.email,
				"OWNERTELNO": (this.participantDate.user.phone) ? this.participantDate.user.phone : null,
				"OWNERCTY": (this.participantDate.user.country?.isoCode) ? this.participantDate.user.country?.isoCode : null,
				"OWNERTOWN": (this.participantDate.user.city) ? this.participantDate.user.city : null,
				"COM": this.description,
				"PAYMENTMETHOD": this.paymentMethod,
				"USERID": this.participantDate.user.id
		])
		PayWayMessage result = message.send('createOrder')

		if (result?.get('success')) {
			this.id = result.get('orderid').toString().toLong()
			return this.save(insert: true, flush: true)
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
