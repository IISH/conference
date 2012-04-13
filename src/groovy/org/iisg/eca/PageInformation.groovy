package org.iisg.eca

/**
 * A Spring bean providing information about the requested page
 */
class PageInformation {
    /**
     * A thread local to the request holding the Page domain class of the current page
     */
    private ThreadLocal<Page> threadLocalPage = new ThreadLocal<Page>()

    /**
     * Sets the page of the current request
     * @param page The page describing the requested page
     */
    public void setPage(Page page) {
        threadLocalPage.set(page)
    }

    /**
     * Returns information about the requested page from the database
     * @return <code>Page</code> domain class
     */
    public Page getPage() {
        threadLocalPage.get()
    }

    /**
     * Removes the <code>Page</code> object from the local thread
     */
    public void removePage() {
        threadLocalPage.remove()
    }
}
