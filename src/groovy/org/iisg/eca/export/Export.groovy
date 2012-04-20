package org.iisg.eca.export

/**
 * An export interface
 */
interface Export {
    /**
     * Returns the content type of the resulting file
     * @return The content type
     */
    public String getContentType()

    /**
     * Parses the element
     */
    public parse()
}