package org.iisg.eca.utils

import groovy.sql.GroovyRowResult
import groovy.transform.CompileStatic

/**
 * Wrapper class of a payment statistic, holding the numbers of participants and the amounts
 * for both the unconfirmed and the confirmed state of a status
 */
@CompileStatic
class PaymentStatistic {
    private long unConfirmedNoParticipants = 0L
    private long unConfirmedAmount = 0L

    private long confirmedNoParticipants = 0L
    private long confirmedAmount = 0L

    PaymentStatistic() { }

    long getUnConfirmedNoParticipants() {
        return unConfirmedNoParticipants
    }

    void setUnConfirmedNoParticipants(long unConfirmedNoParticipants) {
        this.unConfirmedNoParticipants = unConfirmedNoParticipants
    }

    long getUnConfirmedAmount() {
        return unConfirmedAmount
    }

    void setUnConfirmedAmount(long unConfirmedAmount) {
        this.unConfirmedAmount = unConfirmedAmount
    }

    long getConfirmedNoParticipants() {
        return confirmedNoParticipants
    }

    void setConfirmedNoParticipants(long confirmedNoParticipants) {
        this.confirmedNoParticipants = confirmedNoParticipants
    }

    long getConfirmedAmount() {
        return confirmedAmount
    }

    void setConfirmedAmount(long confirmedAmount) {
        this.confirmedAmount = confirmedAmount
    }

    long getTotalNoParticipants() {
        this.unConfirmedNoParticipants + this.confirmedNoParticipants
    }

    long getTotalAmount() {
        this.unConfirmedAmount + this.confirmedAmount
    }

    /**
     * For both the query results of the unconfirmed state and the confirmed state, create payment statistic classes
     * stored in a map with the status of each statistic as the key
     * @param unconfirmed The query results of the unconfirmed state
     * @param confirmed The query results of the confirmed state
     * @return A map holding the payment statistics
     */
    static Map<Long, PaymentStatistic> createMap(List<GroovyRowResult> unconfirmed, List<GroovyRowResult> confirmed) {
        Map<Long, PaymentStatistic> paymentStatisticsMap = new HashMap<Long, PaymentStatistic>()

        unconfirmed.each { row ->
            Long status = new Long(row.get('status').toString())
            PaymentStatistic statistic = paymentStatisticsMap.get(status, new PaymentStatistic())

            statistic.unConfirmedAmount = (row.get('total_amount') != null) ? (Long) row.get('total_amount') : 0L
            statistic.unConfirmedNoParticipants = (row.get('no_participants') != null) ? (Long) row.get('no_participants') : 0L

            paymentStatisticsMap.put(status, statistic)
        }

        confirmed.each { row ->
            Long status = new Long(row.get('status').toString())
            PaymentStatistic statistic = paymentStatisticsMap.get(status, new PaymentStatistic())

            statistic.confirmedAmount = (row.get('total_amount') != null) ? (Long) row.get('total_amount') : 0L
            statistic.confirmedNoParticipants = (row.get('no_participants') != null) ? (Long) row.get('no_participants') : 0L

            paymentStatisticsMap.put(status, statistic)
        }

        paymentStatisticsMap
    }

    /**
     * Get a single payment statistic from the given map by its status
     * @param status The status for which to gain a payment statistic
     * @param statistics The map holding all statistics
     * @return The payment statistic for the given status.
     * If the status does not exists, the default values are returned instead
     */
    static PaymentStatistic getPaymentStatistic(Long status, Map<Long, PaymentStatistic> statistics) {
        if (statistics.containsKey(status)) {
            statistics.get(status)
        }
        else {
            new PaymentStatistic()
        }
    }

    /**
     * For all payment statistics in a map, return a payment statistic
     * that is the total of all statistics in the map
     * @param statistics The map holding all statistics
     * @return A single payment statistic with the totals of all statistics in the given map
     */
    static PaymentStatistic getTotalPaymentStatistic(Map<Long, PaymentStatistic> statistics) {
        PaymentStatistic totalPaymentStatistic = new PaymentStatistic()

        statistics.values().each { statistic ->
            totalPaymentStatistic.unConfirmedNoParticipants += statistic.unConfirmedNoParticipants
            totalPaymentStatistic.unConfirmedAmount += statistic.unConfirmedAmount
            totalPaymentStatistic.confirmedNoParticipants += statistic.confirmedNoParticipants
            totalPaymentStatistic.confirmedAmount += statistic.confirmedAmount
        }

        totalPaymentStatistic
    }
}
