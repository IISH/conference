package org.iisg.eca.utils

/**
 * Collection of all queries to obtain payment statistics
 */
public interface PaymentQueries {
    public static final String PAYMENT_LIST = """
        SELECT * 
		FROM (
			SELECT u.user_id, u.lastname, u.firstname, o.payed, o.payment_method, o.amount, pd.participant_state_id, ps.participant_state
			FROM users AS u
			INNER JOIN participant_date AS pd
			ON u.user_id = pd.user_id
			INNER JOIN participant_states AS ps
			ON pd.participant_state_id = ps.participant_state_id
			LEFT JOIN orders AS o
			ON pd.participant_date_id = o.participant_date_id
			WHERE u.deleted = 0
			AND pd.date_id = :dateId
			AND pd.deleted = 0
			AND (
				pd.participant_state_id IN (1,2)
				OR pd.payment_id IS NOT NULL
			)
			AND o.payed = 1
		
			UNION ALL
		
			SELECT u.user_id, u.lastname, u.firstname, o.payed, o.payment_method, o.amount, pd.participant_state_id, ps.participant_state
			FROM users AS u
			INNER JOIN participant_date AS pd
			ON u.user_id = pd.user_id
			INNER JOIN participant_states AS ps
			ON pd.participant_state_id = ps.participant_state_id
			LEFT JOIN orders AS o
			ON pd.payment_id = o.order_id
			WHERE u.deleted = 0
			AND pd.date_id = :dateId
			AND pd.deleted = 0
			AND (
				pd.participant_state_id IN (1,2)
				OR pd.payment_id IS NOT NULL
			)
			AND (
				o.payed <> 1
				OR o.payed IS NULL
			)
		) a
		ORDER BY lastname ASC, firstname ASC 
    """

    // MAIN PARTS OF THE QUERIES

    public static final String MAIN_BODY_JOIN_PAYMENT_ID = """
        FROM users AS u
		INNER JOIN participant_date AS pd
		ON u.user_id = pd.user_id
		LEFT JOIN orders AS o
		ON pd.payment_id = o.order_id
		WHERE u.deleted = 0
		AND pd.date_id = :dateId
		AND pd.deleted = 0
    """

	public static final String MAIN_BODY_JOIN_USER_ID = """
        FROM users AS u
		INNER JOIN participant_date AS pd
		ON u.user_id = pd.user_id
		LEFT JOIN orders AS o
		ON pd.participant_date_id = o.participant_date_id
		WHERE u.deleted = 0
		AND pd.date_id = :dateId
		AND pd.deleted = 0
    """

    public static final String UNCONFIRMED = " AND o.payed = 0 "
    public static final String CONFIRMED = " AND o.payed > 0 "

    // PAYMENT METHOD QUERIES

    public static final String PAYMENT_METHOD_SELECT =
        "SELECT CAST(o.payment_method AS SIGNED) AS status, COUNT(DISTINCT u.user_id) AS no_participants, SUM(o.amount - o.refunded_amount) AS total_amount "

    public static final String PAYMENT_METHOD_END = """
        AND (o.amount - o.refunded_amount) > 0
		GROUP BY o.payment_method
    """

    public static final String PAYMENT_METHOD_FREE = """
        SELECT 3 AS status, COUNT(DISTINCT u.user_id) AS no_participants, SUM(o.amount - o.refunded_amount) AS total_amount
        ${MAIN_BODY_JOIN_USER_ID}
        ${CONFIRMED}
		AND (o.amount - o.refunded_amount) = 0
    """

    public static final String PAYMENT_METHOD_UNCONFIRMED =
        PAYMENT_METHOD_SELECT +
        MAIN_BODY_JOIN_PAYMENT_ID +
        UNCONFIRMED +
        PAYMENT_METHOD_END

    public static final String PAYMENT_METHOD_CONFIRMED_BANK =
        PAYMENT_METHOD_SELECT +
        MAIN_BODY_JOIN_USER_ID +
        CONFIRMED +
        PAYMENT_METHOD_END

    public static final String PAYMENT_METHOD_CONFIRMED = """
        ${PAYMENT_METHOD_CONFIRMED_BANK}
        UNION ALL
        ${PAYMENT_METHOD_FREE}
    """

    // PAYMENT AMOUNT QUERIES

    public static final String PAYMENT_AMOUNT_SELECT =
        "SELECT (o.amount - o.refunded_amount) AS status, COUNT(DISTINCT u.user_id) AS no_participants, SUM(o.amount - o.refunded_amount) AS total_amount "

    public static final String PAYMENT_AMOUNT_END =
        " GROUP BY (o.amount - o.refunded_amount)"

    public static final String PAYMENT_AMOUNT_UNCONFIRMED =
        PAYMENT_AMOUNT_SELECT +
        MAIN_BODY_JOIN_PAYMENT_ID +
        UNCONFIRMED +
        PAYMENT_AMOUNT_END

    public static final String PAYMENT_AMOUNT_CONFIRMED =
        PAYMENT_AMOUNT_SELECT +
        MAIN_BODY_JOIN_USER_ID +
        CONFIRMED +
        PAYMENT_AMOUNT_END

    public static final String PAYMENT_AMOUNT_LIST = """
        SELECT (o.amount - o.refunded_amount) AS amount
        ${MAIN_BODY_JOIN_USER_ID}
        AND o.amount IS NOT NULL
        GROUP BY (o.amount - o.refunded_amount)
    """

    // PARTICIPANT STATE QUERIES

    public static final String PARTICIPANT_STATE_UNCONFIRMED = """
        SELECT 0 AS status, COUNT(DISTINCT u.user_id) AS no_participants, SUM(o.amount - o.refunded_amount) AS total_amount
        ${MAIN_BODY_JOIN_PAYMENT_ID}
        ${UNCONFIRMED}
        AND pd.participant_state_id NOT IN (1,2)

        UNION ALL

        SELECT 1 AS status, COUNT(DISTINCT u.user_id) AS no_participants, SUM(o.amount - o.refunded_amount) AS total_amount
        ${MAIN_BODY_JOIN_PAYMENT_ID}
        ${UNCONFIRMED}
        AND pd.participant_state_id IN (1,2)
    """

    public static final String PARTICIPANT_STATE_CONFIRMED = """
        SELECT 0 AS status, COUNT(DISTINCT u.user_id) AS no_participants, SUM(o.amount - o.refunded_amount) AS total_amount
        ${MAIN_BODY_JOIN_USER_ID}
        ${CONFIRMED}
        AND pd.participant_state_id NOT IN (1,2)

        UNION ALL

        SELECT 1 AS status, COUNT(DISTINCT u.user_id) AS no_participants, SUM(o.amount - o.refunded_amount) AS total_amount
        ${MAIN_BODY_JOIN_USER_ID}
        ${CONFIRMED}
        AND pd.participant_state_id IN (1,2)
    """

    // TOTALS PARTICIPANTS

    public static final String PARTICIPANTS_TOTAL = """
        SELECT COUNT(DISTINCT u.user_id) AS no_participants
        ${MAIN_BODY_JOIN_USER_ID}
        AND pd.participant_state_id IN (1,2)
    """

    public static final String PARTICIPANTS_TOTAL_PAYED ="""
	    SELECT COUNT(DISTINCT u.user_id) AS no_participants
	    ${MAIN_BODY_JOIN_USER_ID}
	    ${CONFIRMED}
	    AND pd.participant_state_id IN (1,2)
	"""

    public static final String PARTICIPANTS_TOTAL_PAYMENT_NOT_COMPLETE = """
	    SELECT COUNT(DISTINCT u.user_id) AS no_participants
	    ${MAIN_BODY_JOIN_PAYMENT_ID}
	    ${UNCONFIRMED}
	    AND pd.participant_state_id IN (1,2)
	"""

    public static final String PARTICIPANTS_TOTAL_NO_ATTEMPT = """
        SELECT COUNT(DISTINCT u.user_id) AS no_participants
	    ${MAIN_BODY_JOIN_USER_ID}
	    AND pd.participant_state_id IN (1,2)
	    AND (
	        pd.payment_id IS NULL
	        OR pd.payment_id = 0
	    )
    """
}