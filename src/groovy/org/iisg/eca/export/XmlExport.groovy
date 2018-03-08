package org.iisg.eca.export

import groovy.xml.MarkupBuilder

import java.text.DateFormat
import java.text.SimpleDateFormat;

abstract class XmlExport {
    private String description
    private String xml

    XmlExport(String description) {
        this.description = description
    }

    /**
     * Sets up the ground work for creating XML exports
     * @param body The body contents of the XML
     */
    protected void getXml(Closure body) {
        String description = this.description
        DateFormat formatter = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss', Locale.US)
        StringWriter writer = new StringWriter()
        MarkupBuilder builder = new MarkupBuilder(writer)
        builder.doubleQuotes = true

        writer.write('<?xml version="1.0" encoding="utf-8" ?>\n')

        builder.data {
            builder.description(description)
            builder.exportDate(formatter.format(new Date()))

            body(builder)
        }

        xml = writer.toString()
    }

    /**
     * Returns the created XML
     * @return The XML created
     */
    @Override
    String toString() {
        return xml
    }
}
