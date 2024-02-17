package org.iisg.eca.utils

import java.text.SimpleDateFormat

import org.springframework.context.i18n.LocaleContextHolder

import org.springframework.beans.propertyeditors.CustomDateEditor

import org.springframework.beans.PropertyEditorRegistry
import org.springframework.beans.PropertyEditorRegistrar

/**
 * A registrar to make sure to always parse objects of type Date according to the format specified in the i18n bundles
 */
class CustomPropertyEditorRegistrar implements PropertyEditorRegistrar {
    def messageSource

    /**
     * Registers objects of type Date with the specified date format
     */
    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        String dateFormat = messageSource.getMessage("default.date.form.format", null, 'dd/MM/yyyy', LocaleContextHolder.locale)
        registry.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(dateFormat), true));
    }
}
