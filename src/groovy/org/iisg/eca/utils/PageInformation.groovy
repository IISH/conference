package org.iisg.eca.utils

import groovy.transform.CompileStatic
import org.iisg.eca.domain.Page
import org.iisg.eca.domain.EventDate

/**
 * A Spring bean providing information about the requested page
 */
@CompileStatic
class PageInformation {
    /**
     * A thread local to the request holding the Page domain class of the current page
     */
    private ThreadLocal<Page> threadLocalPage = new ThreadLocal<Page>()

    /**
     * A thread local to the request holding the Page domain class of the current page
     */
    private ThreadLocal<EventDate> threadLocalDate = new ThreadLocal<EventDate>()

    /**
     * A thread local to the request holding an identifier for requesting this pages parameters from the session
     */
    private ThreadLocal<String> threadLocalSessionIdentifier = new ThreadLocal<String>()

    /**
     * Sets the page of the current request
     * @param page The page describing the requested page
     */
    public void setPage(Page page) {
        threadLocalPage.set(page)
    }

    /**
     * Sets the event date of the current request
     * @param date The event date of the current request
     */
    public void setDate(EventDate date) {
        threadLocalDate.set(date)
    }

    /**
     * Sets the session identifier of the current request
     * @param sessionIdentifier Session identifier for the parameters of this page
     */
    public void setSessionIdentifier(String sessionIdentifier) {
        threadLocalSessionIdentifier.set(sessionIdentifier)
    }

    /**
     * Returns information about the requested page from the database
     * @return <code>Page</code> domain class
     */
    public Page getPage() {
        threadLocalPage.get()
    }

    /**
     * Returns the event date of the current request
     * @return <code>EventDate</code> domain class
     */
    public EventDate getDate() {
        threadLocalDate.get()
    }

    /**
     * Returns the session identifier of the current request
     * @return <code>String</code> Session identifier for the parameters of this page
     */
    public String getSessionIdentifier() {
        threadLocalSessionIdentifier.get()
    }

    /**
     * Removes the <code>Page</code> object from the local thread
     */
    public void removePage() {
        threadLocalPage.remove()
    }

    /**
     * Removes the <code>EventDate</code> object from the local thread
     */
    public void removeDate() {
        threadLocalDate.remove()
    }

    /**
     * Removes the <code>String</code> object from the local thread
     */
    public void removeSessionIdentifier() {
        threadLocalSessionIdentifier.remove()
    }
}
