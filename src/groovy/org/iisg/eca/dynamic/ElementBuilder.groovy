package org.iisg.eca.dynamic

import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty

abstract class ElementBuilder {
    protected static final String RESULTS = "results"

    protected MarkupBuilder builder;

    /**
     * Builds all of the buttons in the given list
     * @param buttons The list of buttons to be build
     */
    protected void buildButtons(List<Button> buttons) {
        buttons.each { button ->
            switch (button.type) {
                case Button.Type.SAVE:
                    builder.input(type: "submit", name: "btn_${button.name}", class: "btn_${button.name}", value: "\${message(code: 'default.button.${button.name}.label')}")
                    builder."g:if"(test: "\${params.action != 'create'}") {
                        builder.input(type: "submit", name: "btn_${button.name}_close", class: "btn_${button.name}_close", value: "\${message(code: 'default.button.${button.name}.close.label')}")
                    }
                    break
                case Button.Type.BACK:
                    builder."eca:link"(previous: "true") {
                        builder."g:message"(code: "default.button.${button.name}.label")
                    }
                    break
                case Button.Type.DELETE:
                    builder."eca:link"(controller: "\${params.controller}", action: "delete", id: "\${params.id}", class: "btn_delete") {
                        builder."g:message"(code: "default.deleted.label")
                    }
                    break
                case Button.Type.URL:
                default:
                    builder."eca:ifUserHasAccess"(controller: button.controller, action: button.action) {
                        builder."eca:link"(controller: button.controller, action: button.action, id: button.id) {
                            builder."g:message"(code: "default.button.${button.action}.label")
                        }
                    }
            }
        }
    }

    /**
     * Returns the i18n lookup code for the given column property
     * @param property The property of a column which needs an i18n label
     */
    protected static getCode(GrailsDomainClassProperty property) {
        if (property.name.equalsIgnoreCase("deleted")) {
            return "default.${property.name}.label"
        }
        else if (property.manyToOne || property.oneToOne || property.oneToMany || property.manyToMany) {
            return "${property.referencedDomainClass.propertyName}.multiple.label"
        }
        else {
            return "${property.domainClass.propertyName}.${property.name}.label"
        }
    }

    /**
     * Returns the i18n fallback lookup code for the given column property, in case the first one does not exist
     * @param property The property of a column which needs a secondary i18n label
     */
    protected static getFbCode(GrailsDomainClassProperty property) {
        "${property.domainClass.propertyName}.${property.name}.label"
    }
}
