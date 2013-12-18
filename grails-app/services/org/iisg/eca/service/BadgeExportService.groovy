package org.iisg.eca.service

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.Extra

import org.iisg.eca.export.Export
import org.iisg.eca.export.XlsMapExport

import groovy.sql.Sql
import java.text.SimpleDateFormat
import org.hibernate.impl.SessionImpl
import org.springframework.context.i18n.LocaleContextHolder

/**
 * Service that takes care of creating the XLS export for badges
 */
class BadgeExportService {
    def sessionFactory
    def sessionFactory_payWay
    def dataSource_payWay
    def messageSource
    def pageInformation

    /**
     * Create a specific export for the creation of badges
     * @return An export which can be used to create the XLS file
     */
    Export getParticipantsExport() {
        // Preparation
        SimpleDateFormat formatter = new SimpleDateFormat('EEEEE, d MMMMM', LocaleContextHolder.locale)
        Map<Long, String> days = Day.list().collectEntries { day ->
            [(day.id) : formatter.format(day.day)]
        }
        Map<Long, String> extras = Extra.list().collectEntries { extra ->
            [(extra.id) : extra.title]
        }

        String trueText = messageSource.getMessage('default.boolean.true', null, LocaleContextHolder.locale)
        String falseText = messageSource.getMessage('default.boolean.false', null, LocaleContextHolder.locale)

        List<String> columns = ['user_id', 'title', 'lastname', 'firstname', 'organisation', 'department',
                'name_english', 'payed', 'willpaybybank', 'amount', 'name', 'day']
        columns += extras.values()

        List<String> columnNames = [
                'ID',
                messageSource.getMessage('title.label', null, LocaleContextHolder.locale),
                messageSource.getMessage('user.firstName.label', null, LocaleContextHolder.locale),
                messageSource.getMessage('user.lastName.label', null, LocaleContextHolder.locale),
                messageSource.getMessage('user.organisation.label', null, LocaleContextHolder.locale),
                messageSource.getMessage('user.department.label', null, LocaleContextHolder.locale),
                messageSource.getMessage('user.country.label', null, LocaleContextHolder.locale),
                messageSource.getMessage('order.status.payed.label', null, LocaleContextHolder.locale),
                messageSource.getMessage('order.method.bank.label', null, LocaleContextHolder.locale),
                messageSource.getMessage('order.amount.label', null, LocaleContextHolder.locale),
                messageSource.getMessage('feeState.label', null, LocaleContextHolder.locale),
                messageSource.getMessage('day.label', null, LocaleContextHolder.locale)
        ]
        columnNames += extras.values()

        // Obtain results and transform them
        Sql sql = new Sql(dataSource_payWay)
        String dbName = ((SessionImpl) sessionFactory.currentSession).connection().catalog
        String dbNamePayWay = ((SessionImpl) sessionFactory_payWay.currentSession).connection().catalog

        List<Map> results = sql.rows(getParticipantsSQL(dbName, dbNamePayWay), [dateId: pageInformation.date.id])
        results.each { Map row ->
            if (row.payed == null) {
                row.put('payed', falseText)
            }
            else {
                row.put('payed', row.payed.toString().equals('1') ? trueText : falseText)
            }

            if (row.willpaybybank != null) {
                row.put('willpaybybank', row.willpaybybank.toString().equals('true') ? trueText : falseText)
            }

            List<Long> dayIds = row.days ? row.days.split(',')*.toLong() : []
            row.remove('days')
            if (dayIds.size() == 1) {
                row.put('day', days.get(dayIds.first()))
            }
            else {
                row.put('day', '')
            }

            List<Long> extraIds = row.extras ? row.extras.split(',')*.toLong() : []
            row.remove('extras')
            extras.each { Long id, String name ->
                if (extraIds.contains(id)) {
                    row.put(name, trueText)
                }
                else {
                    row.put(name, falseText)
                }
            }
        }

        // Create XLS export
        String title = messageSource.getMessage('participantDate.badges.label', null, LocaleContextHolder.locale)
        return new XlsMapExport(columns, results, title, columnNames)
    }

    /**
     * The SQL to obtain all participant information for the badges
     * @param dbName The database name of conference
     * @param dbNamePayWay The database name of payway
     * @return The SQL, ready to be send to the database
     */
    private String getParticipantsSQL(String dbName, String dbNamePayWay) {
        ''' SELECT u.user_id, u.title, u.lastname, u.firstname, u.organisation, u.department, c.name_english,
            o.payed, o.willpaybybank, o.amount, fs.name,
            CAST(GROUP_CONCAT(DISTINCT d.day_id ORDER BY d.day_number) AS CHAR) AS days,
            CAST(GROUP_CONCAT(DISTINCT e.extra_id ORDER BY e.extra) AS CHAR) AS extras
            FROM `db-name`.users AS u
            INNER JOIN `db-name`.participant_date AS pd
            ON u.user_id = pd.user_id
            LEFT JOIN `db-name`.countries AS c
            ON u.country_id = c.country_id
            LEFT JOIN `db-name-payway`.orders AS o
            ON pd.payment_id = o.ID
            INNER JOIN `db-name`.fee_states AS fs
            ON pd.fee_state_id = fs.fee_state_id
            LEFT JOIN `db-name`.participant_day AS pday
            ON u.user_id = pday.user_id
            LEFT JOIN `db-name`.days AS d
            ON pday.day_id = d.day_id
            LEFT JOIN `db-name`.participant_date_extra AS pde
            ON pd.participant_date_id = pde.participant_date_id
            LEFT JOIN `db-name`.extras AS e
            ON pde.extra_id = e.extra_id
            WHERE u.deleted = false
            AND pd.date_id = :dateId
            AND pd.deleted = false
            AND pday.date_id = :dateId
            AND pday.deleted = false
            AND d.date_id = :dateId
            AND d.deleted = false
            AND e.date_id = :dateId
            AND e.deleted = false
            AND (   pd.participant_state_id IN (1,2)
                    OR (pd.payment_id IS NOT NULL AND payment_id > 0))
            GROUP BY u.user_id
            ORDER BY u.lastname ASC, u.firstname ASC'''
                .replace('db-name-payway', dbNamePayWay).replace('db-name', dbName)
    }
}
