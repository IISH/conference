package org.iisg.eca.dynamic

import groovy.xml.MarkupBuilder
import org.springframework.core.GenericCollectionTypeResolver

/**
 * An implementation of the renderEditor template as a class and adapted to our needs
 * Tries to render the columns in a form based on their properties as defined in the belonging domain class
 */
class RenderEditor {
    private MarkupBuilder builder
    private String resultsLink

    /**
     * A render editor for a specific page
     * @param builder Where to write the resulting html with and to
     * @param resultsLink The reference to the <code>DynamicPageResults</code> variable
     */
    RenderEditor(MarkupBuilder builder, String resultsLink) {
        this.builder = builder
        this.resultsLink = resultsLink
    }
    
    /**
     * Renders the column based on the properties as defined in the domain class
     * @param column The column to render
     */
    void render(Column column) {
        render(column, [:])
    }

    /**
     * Renders the column based on the properties as defined in the domain class
     * @param column The column to render
     * @param extraProperties Extra properties to apply
     */
    void render(Column column, Map extraProperties) {
        if (column.property.type == Boolean || column.property.type == boolean) {
            renderBooleanEditor(column, extraProperties)
        }
        else if (column.property.type && Number.isAssignableFrom(column.property.type) || (column.property.type?.isPrimitive() && column.property.type != boolean)) {
            renderNumberEditor(column, extraProperties)
        }
        else if (column.property.type == String) {
            renderStringEditor(column, extraProperties)
        }
        else if (column.property.type == Date || column.property.type == java.sql.Date || column.property.type == java.sql.Time || column.property.type == Calendar) {
            renderDateEditor(column, extraProperties)
        }
        else if (column.property.type == URL) {
            renderStringEditor(column, extraProperties)
        }
        else if (column.property.type && column.property.isEnum()) {
            renderEnumEditor(column, extraProperties)
        }
        else if (column.property.type == TimeZone) {
            renderSelectTypeEditor("timeZone", column, extraProperties)
        }
        else if (column.property.type == Locale) {
            renderSelectTypeEditor("locale", column, extraProperties)
        }
        else if (column.property.type == Currency) {
            renderSelectTypeEditor("currency", column, extraProperties)
        }
        else if (column.property.type == ([] as Byte[]).class) {
            renderByteArrayEditor(column, extraProperties)
        }
        else if (column.property.type == ([] as byte[]).class) {
            renderByteArrayEditor(column, extraProperties)
        }
        else if (column.property.manyToOne || column.property.oneToOne) {
            renderManyToOne(column, extraProperties)
        }
        else if ((column.property.oneToMany && !column.property.bidirectional) || (column.property.manyToMany && column.property.isOwningSide())) {
            renderManyToMany(column, extraProperties)
        }
        else if (column.property.oneToMany) {
            renderOneToMany(column, extraProperties)
        }
    }

    private String getValue(Column column) {
        "\${${getValueReady(column)}}"
    }
    
    private String getValueReady(Column column) {
        if ((column.parent instanceof Column) && (column.parent.multiple)) {
            "instance?.${column.name}"
        }
        else {
            "${getResult(column)}?.${column.name}"
        }
    }

    private String getResult(Column column) {
        "${resultsLink}.get(${column.root.eid}).get('${column.domainClass.name}')"
    }

    private String getName(Column column) {
        if ((column.parent instanceof Column) && (column.parent.multiple)) {
            "${column.domainClass.name}_\${i}.${column.name}"
        }
        else {
            "${column.domainClass.name}.${column.name}"
        }
    }

    private HashMap<String, String> createPropertiesMap(Column column) {
        Map props = new HashMap<String, String>()

        props.put("name",   getName(column))
        props.put("id",     getName(column))
        props.put("value",  getValue(column))

        props
    }
    
    public void renderHidden(Column column, Map extraProperties) {
        Map props = createPropertiesMap(column)   
        props.put("type", "hidden")
        builder."input"(props + extraProperties)
    }
    
    private void renderEnumEditor(Column column, Map extraProperties) {
        Map props = createPropertiesMap(column)
        props.put("from",   "${column.property.type.name}?.values()")
        props.put("keys",   "${column.property.type.name}.values()*.name()}")

        (isOptional(column)) ?: props.put("required", "required")
        renderNoSelection(column, props)

        builder."g:select"(props + extraProperties)
    }

    private void renderStringEditor(Column column, Map extraProperties) {
        if (!column.constrainedProperty) {
            builder.input(type: "text", name: getName(column), value: getValue(column))
        }
        else {
            Map props = createPropertiesMap(column)

            if (     (column.textarea || ("textarea" == column.constrainedProperty.widget)) ||
                     ((!column.constrainedProperty.maxSize || column.constrainedProperty.maxSize > 255) &&
                      (!column.constrainedProperty.password && !column.constrainedProperty.inList))) {
                props.remove("value")

                if (column.textarea == 'large') {
                    props.put("rows", "20")
                    props.put("style", "width:70%;")
                }
                else {
                    props.put("cols", "40")
                    if (column.constrainedProperty.maxSize) {
                        props.put("rows", Math.min((column.constrainedProperty.maxSize/30).intValue(), 5).toString())
                    }
                    else {
                        props.put("rows", "5")
                    }
                }

                (!column.constrainedProperty.maxSize)   ?: props.put("maxlength",   column.constrainedProperty.maxSize.toString())
                (isOptional(column))                    ?: props.put("required",    "required")

                builder.textarea(props + extraProperties, getValue(column))
            }
            else if (column.constrainedProperty.inList) {
                props.put("from",               "\${${getResult(column)}.constraints.${column.property.name}.inList}")
                props.put("valueMessagePrefix", getValue(column))

                (isOptional(column)) ?: props.put("required", "required")
                renderNoSelection(column, props)

                builder."g:select"(props + extraProperties)
            }
            else {
                (!column.constrainedProperty.maxSize)   ?: props.put("maxlength",   column.constrainedProperty.maxSize)
                (column.constrainedProperty.editable)   ?: props.put("readonly",    "readonly")
                (!column.constrainedProperty.matches)   ?: props.put("pattern",     "\${${getResult(column)}.constraints.${column.property.name}.matches}")
                (isOptional(column))                    ?: props.put("required",    "required")

                if (column.constrainedProperty.password) {
                    props.put("type", "password")
                }
                else if (column.constrainedProperty.url) {
                    props.put("type", "url")
                }
                else if (column.constrainedProperty.email) {
                    props.put("type", "email")
                }
                else {
                    props.put("type", "text")
                }

                builder.input(props + extraProperties)
            }
        }
    }

    private void renderByteArrayEditor(Column column, Map extraProperties) {
        Map props = createPropertiesMap(column)
        props.put("type", "file")
        builder.input(props + extraProperties)
    }

    private void renderManyToOne(Column column, Map extraProperties) {
        if (column.property.association) {
            Map props = createPropertiesMap(column)
            props.put("value",      "\${${getValueReady(column)}?.id}")
            props.put("name",       "${getName(column)}.id")
            props.put("id",         column.property.name)
            props.put("from",       "\${${column.property.type.name}.list()}")
            props.put("optionKey",  "id")

            (isOptional(column)) ?: props.put("required", "required")
            renderNoSelection(column, props)

            builder."g:select"(props + extraProperties)
        }
    }

    private void renderManyToMany(Column column, Map extraProperties) {
        Class cls = column.property.referencedDomainClass?.clazz

        if (cls == null) {
            if (column.property.type instanceof Collection) {
                cls = GenericCollectionTypeResolver.getCollectionType(column.property.type)
            }
        }

        if (cls != null) {
            Map props = createPropertiesMap(column)
            props.put("value",      "\${${getValueReady(column)}?.id}")
            props.put("from",       "\${${cls.name}.list()}")
            props.put("multiple",   "multiple")
            props.put("optionKey",  "id")
            props.put("size",       "5")

            (isOptional(column)) ?: props.put("required", "required")

            builder."g:select"(props + extraProperties)
        }
    }

    private void renderOneToMany(Column column, Map extraProperties) {
        builder.ul {
            builder."g:each"(in: "\${${getResult(column)}.${column.property.name}?}", var: "instance") {
                builder.li {
                    builder.a(href: "../${column.property.referencedDomainClass.propertyName}/show/\${instance.id}", "\${instance?.encodeAsHTML()}")
                }
            }
            builder.li(class: "add") {
                builder."g:link"(controller: "${column.property.referencedDomainClass.propertyName}", action: "create", params: "['${column.domainClass.name}.id': ${getResult(column)}?.id]") {
                    builder."g:message"(code: "default.add.label", args: "[g.message(code: \"${property.referencedDomainClass.name}.label)\"]")
                }
            }
        }
    }

    private void renderNumberEditor(Column column, Map extraProperties) {
        if (!column.constrainedProperty) {
            if (column.property.type == Byte) {
                Map props = [name: getName(column), from: "\${-128..127}", value: getValue(column)]
                builder."g:select"(props + extraProperties)
            }
            else {
                Map props = [type: "number", name: getName(column), value: getValue(column)]
                builder."g:field"(props + extraProperties)
            }
        }
        else {
            Map props = createPropertiesMap(column)

            (isOptional(column)) ?: props.put("required", "required")
            renderNoSelection(column, props)

            if (column.constrainedProperty.range) {
                props.put("from",   "\${${column.constrainedProperty.range.from}..${column.constrainedProperty.range.to}}")

                builder."g:select"(props + extraProperties)
            }
            else if (column.constrainedProperty.inList) {
                props.put("from",               "\${${getResult(column)}.constraints.${column.property.name}.inList}")
                props.put("valueMessagePrefix", getValue(column))

                builder."g:select"(props + extraProperties)
            }
            else {
                props.put("type",   "number")

                (!column.constrainedProperty.scale) ?: props.put("step",    BigDecimal.valueOf(1).movePointLeft(column.constrainedProperty.scale).toString())
                (!column.constrainedProperty.min)   ?: props.put("min",     column.constrainedProperty.min)
                (!column.constrainedProperty.max)   ?: props.put("max",     column.constrainedProperty.max)

                builder."g:field"(props + extraProperties)
            }
        }
     }

    private void renderBooleanEditor(Column column, Map extraProperties) {
        if (!column.constrainedProperty) {
            Map props = [type: "checkbox", name: getName(column), value: getValue(column)]
            builder.input(props + extraProperties)
        }
        else {
            Map props = createPropertiesMap(column)

            (!column.constrainedProperty.widget) ?: props.put("widget", column.constrainedProperty.widget)

            column.constrainedProperty.attributes.each { k, v ->
                props.put(k, v)
            }

            builder."g:checkBox"(props + extraProperties)
        }
    }

    private void renderDateEditor(Column column, Map extraProperties) {
        String precision = (column.property.type == Date || column.property.type == java.sql.Date || column.property.type == Calendar) ? "day" : "minute"

        if (!column.constrainedProperty) {
            Map props = [name: getName(column), precision: precision, value: getValue(column)]
            builder."g:datePicker"(props + extraProperties)
        }
        else {
            if (!column.constrainedProperty.editable) {
                builder.span(extraProperties, getValue(column))
            }
            else {
                Map props = createPropertiesMap(column)
                props.put("type",           "text")
                props.put("class",          "datepicker")
                props.put("placeholder",    "\${g.message(code: 'default.date.form.format').toLowerCase()}")
                props.put("value",          "\${g.formatDate(formatName: 'default.date.form.format', date: ${getValueReady(column)})}")

                (isOptional(column)) ?: props.put("required", "required")

                builder.input(props + extraProperties)
            }
        }
    }

    private void renderSelectTypeEditor(String type, Column column, Map extraProperties) {
        if (!column.constrainedProperty) {
            Map props = [name: getName(column), value: getValue(column)]
            builder."g:${type}Select"(props + extraProperties)
        }
        else {
            Map props = createPropertiesMap(column)

            (!column.constrainedProperty.widget) ?: props.put("widget", column.constrainedProperty.widget)
            renderNoSelection(column, props)

            column.constrainedProperty.attributes.each { k, v ->
                props.put(k, v)
            }

            builder."g:${type}Select"(props + extraProperties)
        }
    }

    private static void renderNoSelection(Column column, Map<String, String> map) {
        if (isOptional(column)) {
            if (column.property.manyToOne || column.property.oneToOne) {
                map.put("noSelection",  "['null': '']")
            }
            else if (column.property.type == Date || column.property.type == java.sql.Date || column.property.type == java.sql.Time || column.property.type == Calendar) {
                map.put("default",      "none")
                map.put("noSelection",  "['': '']")
            }
            else {
                map.put("noSelection",  "['': '']")
            }
        }
    }
    
    private static void cleanMap(Map map) {
        map.values().removeAll([null, 'null', ''] as Set)
    }

    static boolean isRequired(Column column) {
        !isOptional(column)
    }

    static boolean isOptional(Column column) {
        if (!column.constrainedProperty) {
            return false
        }
        else {
            column.constrainedProperty.nullable || (column.constrainedProperty.propertyType == String && column.constrainedProperty.blank) || column.constrainedProperty.propertyType in [boolean, Boolean]
        }
    }
}
