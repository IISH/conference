package org.iisg.eca.domain.payway

import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.User
import org.springframework.context.i18n.LocaleContextHolder

/**
 * The details of an order in PayWay
 */
class Order {
    def messageSource

    static final int ORDER_NOT_PAYED = 0
    static final int ORDER_PAYED = 1
    static final int ORDER_FULL_REFUND = 2

    Long projectId
    String orderCode
    String userId
    String currency
    long amount = 0L
    int payed = Order.ORDER_NOT_PAYED
    boolean willPayByBank = false
    Date createdAt = new Date()
    Date updatedAt = new Date()
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
        payed               column: 'payed'
        willPayByBank       column: 'willpaybybank'
        createdAt           column: 'created_at'
        updatedAt           column: 'updated_at'
        description         column: 'description',  type: 'text'
    }

    static constraints = {
        orderCode           maxSize: 50,    nullable: true
        userId              maxSize: 50,    nullable: true
        currency            maxSize: 10,    blank: false
        amount              min: 0L
        description                         blank: false
    }

    def beforeUpdate() {
        updatedAt = new Date()
    }

    BigDecimal getAmountAsBigDecimal() {
        new BigDecimal(amount).movePointLeft(2)
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

        Order.withCriteria {
            eq('projectId', projectId.value.toLong())
            eq('userId', user.id.toString())

            ge('createdAt', minimumDate)

            order('id', 'desc')
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
            case ORDER_FULL_REFUND:
                return messageSource.getMessage('order.status.full.refund.label', null, LocaleContextHolder.locale)
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
            this.payed = ORDER_PAYED
            participant.paymentId = this.id

            return true
        }

        return false
    }

    @Override
    String toString() {
        return "$orderCode: $amount $currency"
    }
}
