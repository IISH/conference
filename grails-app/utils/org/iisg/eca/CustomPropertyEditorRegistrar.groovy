package org.iisg.eca

import java.text.SimpleDateFormat

import org.springframework.beans.PropertyEditorRegistry
import org.springframework.beans.PropertyEditorRegistrar
import org.springframework.beans.propertyeditors.CustomDateEditor

/**
 * A registrar to make sure to always parse objects of type Date according to the format specified here
 */
class CustomPropertyEditorRegistrar implements PropertyEditorRegistrar {
    private static final String DATE_FORMAT = "MM/dd/yyyy"

    /**
     * Registers objects of type Date with the specified date format
     */
    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        registry.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(DATE_FORMAT), true));
    }
}
