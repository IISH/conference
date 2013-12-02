package org.iisg.eca.converter

import java.text.SimpleDateFormat
import org.grails.databinding.converters.ValueConverter
import org.springframework.context.i18n.LocaleContextHolder

/**
 * Allows forms to send the date in the format of the currently used locale
 * and parse them correctly to a <code>Date</code> object
 */
class DateConverter implements ValueConverter {
    def messageSource

    /**
     * Will convert a <code>String<code> with the date
     * @param value The value to convert
     * @return Whether this is a <code>String</code>
     */
    @Override
    boolean canConvert(Object value) {
        value instanceof String
    }

    /**
     * Converts the date as a <code>String</code> object to a <code>Date</code> object
     * @param value The value to convert
     * @return The resulting <code>Date</code> object
     */
    @Override
    Object convert(Object value) {
        String dateFormat = messageSource.getMessage("default.date.form.format", null, 'dd/MM/yyyy', LocaleContextHolder.locale)
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat)
        formatter.parse(value.toString())
    }

    /**
     * The class of the resulting object
     * @return The Date class
     */
    @Override
    Class<?> getTargetType() {
        Date.class
    }
}
