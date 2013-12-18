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
     * Returns the extension of the resulting file
     * @return The extension
     */
    public String getExtension()

    /**
     * Returns the title of the resulting file
     * @return The title
     */
    public String getTitle()

    /**
     * Parses the element
     */
    public parse()
}