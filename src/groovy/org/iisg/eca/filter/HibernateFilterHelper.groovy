package org.iisg.eca.filter

import groovy.transform.CompileStatic
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.iisg.eca.domain.EventDate

/**
 * Helper class to enable/disable pre-defined Hibernate filters
 */
@CompileStatic
class HibernateFilterHelper {
    SessionFactory sessionFactory

    void enableEventDateFilter(EventDate eventDate, Session session = null) {
        session = (session) ?: sessionFactory.currentSession
        session.enableFilter('eventDateFilter').setParameter('dateId', eventDate.id)
        session.enableFilter('eventFilter').setParameter('eventId', eventDate.event.id)
    }

    void disableEventDateFilter(Session session = null) {
        session = (session) ?: sessionFactory.currentSession
        session.disableFilter('eventDateFilter')
        session.disableFilter('eventFilter')
    }

    void enableSoftDeleteFilter(Session session = null) {
        session = (session) ?: sessionFactory.currentSession
        session.enableFilter('softDeleteFilter')
    }

    void disableSoftDeleteFilter(Session session = null) {
        session = (session) ?: sessionFactory.currentSession
        session.disableFilter('softDeleteFilter')
    }
}
