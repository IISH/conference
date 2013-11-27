package org.iisg.eca.utils

/**
 * Collection of all queries to obtain payment statistics
 */
public interface PaymentQueries {
    public static final String PAYMENT_LIST = """
        SELECT u.user_id, u.lastname, u.firstname, o.payed, o.willpaybybank, o.amount, pd.participant_state_id, ps.participant_state
        FROM `db-name`.users AS u
        INNER JOIN `db-name`.participant_date AS pd
        ON u.user_id = pd.user_id
        INNER JOIN `db-name`.participant_states AS ps
        ON pd.participant_state_id = ps.participant_state_id
        LEFT JOIN `db-name-payway`.orders AS o
        ON pd.payment_id = o.ID
        WHERE u.deleted = false
        AND pd.date_id = :dateId
        AND pd.deleted = false
        ORDER BY u.lastname ASC, u.firstname ASC
    """

    // MAIN PARTS OF THE QUERIES

    public static final String MAIN_BODY = """
        FROM `db-name`.users AS u
        INNER JOIN `db-name`.participant_date AS pd
        ON u.user_id = pd.user_id
        LEFT JOIN `db-name-payway`.orders AS o
        ON pd.payment_id = o.ID
        WHERE u.deleted = false
        AND pd.date_id = :dateId
        AND pd.deleted = false
    """

    public static final String UNCONFIRMED = " AND o.payed <> 1 "
    public static final String CONFIRMED = " AND o.payed = 1 "

    // PAYMENT METHOD QUERIES

    public static final String PAYMENT_METHOD_SELECT =
        "SELECT CAST(o.willpaybybank AS SIGNED) AS status, count(o.willpaybybank) AS no_participants, sum(o.amount) AS total_amount "

    public static final String PAYMENT_METHOD_END = """
        AND o.amount > 0
        GROUP BY o.willpaybybank
    """

    public static final String PAYMENT_METHOD_FREE = """
        SELECT 2 AS status, count(*) AS no_participants, sum(o.amount) AS total_amount
        ${MAIN_BODY}
        AND o.amount = 0
    """

    public static final String PAYMENT_METHOD_UNCONFIRMED =
        PAYMENT_METHOD_SELECT +
        MAIN_BODY +
        UNCONFIRMED +
        PAYMENT_METHOD_END

    public static final String PAYMENT_METHOD_CONFIRMED_BANK =
        PAYMENT_METHOD_SELECT +
        MAIN_BODY +
        CONFIRMED +
        PAYMENT_METHOD_END

    public static final String PAYMENT_METHOD_CONFIRMED = """
        ${PAYMENT_METHOD_CONFIRMED_BANK}
        UNION ALL
        ${PAYMENT_METHOD_FREE}
    """

    // PAYMENT AMOUNT QUERIES

    public static final String PAYMENT_AMOUNT_SELECT =
        "SELECT o.amount AS status, count(o.amount) AS no_participants, sum(o.amount) AS total_amount "

    public static final String PAYMENT_AMOUNT_END =
        " GROUP BY o.amount"

    public static final String PAYMENT_AMOUNT_UNCONFIRMED =
        PAYMENT_AMOUNT_SELECT +
        MAIN_BODY +
        UNCONFIRMED +
        PAYMENT_AMOUNT_END

    public static final String PAYMENT_AMOUNT_CONFIRMED =
        PAYMENT_AMOUNT_SELECT +
        MAIN_BODY +
        CONFIRMED +
        PAYMENT_AMOUNT_END

    public static final String PAYMENT_AMOUNT_LIST = """
        SELECT o.amount
        ${MAIN_BODY}
        AND o.amount IS NOT NULL
        GROUP BY o.amount
    """

    // PARTICIPANT STATE QUERIES

    public static final String PARTICIPANT_STATE_UNCONFIRMED = """
        SELECT 0 AS status, count(*) AS no_participants, sum(o.amount) AS total_amount
        ${MAIN_BODY}
        ${UNCONFIRMED}
        AND pd.participant_state_id NOT IN (1,2)

        UNION ALL

        SELECT 1 AS status, count(*) AS no_participants, sum(o.amount) AS total_amount
        ${MAIN_BODY}
        ${UNCONFIRMED}
        AND pd.participant_state_id IN (1,2)
    """

    public static final String PARTICIPANT_STATE_CONFIRMED = """
        SELECT 0 AS status, count(*) AS no_participants, sum(o.amount) AS total_amount
        ${MAIN_BODY}
        ${CONFIRMED}
        AND pd.participant_state_id NOT IN (1,2)

        UNION ALL

        SELECT 1 AS status, count(*) AS no_participants, sum(o.amount) AS total_amount
        ${MAIN_BODY}
        ${CONFIRMED}
        AND pd.participant_state_id IN (1,2)
    """

    // TOTALS PARTICIPANTS

    public static final String PARTICIPANTS_TOTAL = """
        SELECT count(*) AS no_participants
        ${MAIN_BODY}
    """

    public static final String PARTICIPANTS_TOTAL_PAYED =
        PARTICIPANTS_TOTAL +
        CONFIRMED

    public static final String PARTICIPANTS_TOTAL_PAYMENT_NOT_COMPLETE = """
        ${PARTICIPANTS_TOTAL}
        AND o.ID IS NOT NULL
        ${UNCONFIRMED}
    """

    public static final String PARTICIPANTS_TOTAL_NO_ATTEMPT = """
        ${PARTICIPANTS_TOTAL}
        AND (
            pd.payment_id IS NULL
            OR pd.payment_id = 0
        )
    """
}