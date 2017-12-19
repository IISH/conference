package org.iisg.eca.export

import groovy.transform.CompileStatic

/**
 * An export interface
 */
@CompileStatic
interface Export {
    /**
     * Returns the content type of the resulting file
     * @return The content type
     */
    String getContentType()

    /**
     * Returns the extension of the resulting file
     * @return The extension
     */
    String getExtension()

    /**
     * Returns the title of the resulting file
     * @return The title
     */
    String getTitle()

    /**
     * Parses the element
     */
    Object parse()
}