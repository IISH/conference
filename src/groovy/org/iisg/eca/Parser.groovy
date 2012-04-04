package org.iisg.eca

/**
 * A parser for a <code>PageElement</code>
 */
interface Parser {
    /**
     * Returns the content type of the resulting file
     * @return
     */
    public getContentType()

    /**
     * Parses the element
     */
    public parse()
}