package org.iisg.eca

import groovy.xml.MarkupBuilder

import org.codehaus.groovy.grails.validation.ConstrainedProperty

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty

import org.springframework.core.GenericCollectionTypeResolver

/**
 * An implementation of the renderEditor template as a class and adapted to our needs
 * Tries to render the columns in a form based on their properties as defined in the belonging domain class
 */
class RenderEditor {
    private GrailsDomainClass domainClass
    private MarkupBuilder builder
    private int eid

    /**
     * A render editor for the specified domain class
     * @param domainClass the domain class to look the properties and constraints
     * @param builder Where to write the resulting html to
     * @param eid The id of the element in question
     */
    RenderEditor(GrailsDomainClass domainClass, MarkupBuilder builder, int eid) {
        this.domainClass = domainClass
        this.builder = builder
        this.eid = eid
    }

    /**
     * Renders the column based on the properties as defined in the domain class
     * @param columnName The name of the column to render
     */
    void render(String columnName) {
        GrailsDomainClassProperty p = domainClass.getPropertyByName(columnName)
        ConstrainedProperty cp = domainClass.constrainedProperties[columnName]

        if (p.type == Boolean || p.type == boolean) {
            renderBooleanEditor(p, cp)
        }
        else if (p.type && Number.isAssignableFrom(p.type) || (p.type?.isPrimitive() && p.type != boolean)) {
            renderNumberEditor(p, cp)
        }
        else if (p.type == String) {
            renderStringEditor(p, cp)
        }
        else if (p.type == Date || p.type == java.sql.Date || p.type == java.sql.Time || p.type == Calendar) {
            renderDateEditor(p, cp)
        }
        else if (p.type == URL) {
            renderStringEditor(p, cp)
        }
        else if (p.type && p.isEnum()) {
            renderEnumEditor(p, cp)
        }
        else if (p.type == TimeZone) {
            renderSelectTypeEditor("timeZone", p, cp)
        }
        else if (p.type == Locale) {
            renderSelectTypeEditor("locale", p, cp)
        }
        else if (p.type == Currency) {
            renderSelectTypeEditor("currency", p, cp)
        }
        else if (p.type == ([] as Byte[]).class) {
            renderByteArrayEditor(p)
        }
        else if (p.type == ([] as byte[]).class) {
            renderByteArrayEditor(p)
        }
        else if (p.manyToOne || p.oneToOne) {
            renderManyToOne(p, cp)
        }
        else if ((p.oneToMany && !p.bidirectional) || (p.manyToMany && p.isOwningSide())) {
            renderManyToMany(p, cp)
        }
        else if (p.oneToMany) {
            renderOneToMany(p)
        }
    }

    private String getValue(GrailsDomainClassProperty property) {
        "\${${getValueReady(property)}}"
    }

    private String getValueReady(GrailsDomainClassProperty property) {
        "${getResult()}['${property.name}']"
    }

    private String getResult() {
        "page.elements.get(${eid}).getResultByDomainClassName('${domainClass.propertyName}')"
    }

    private String getName(GrailsDomainClassProperty property) {
        "${domainClass.propertyName}.${property.name}"
    }

    private HashMap<String, String> createPropertiesMap(GrailsDomainClassProperty property) {
        Map props = new HashMap<String, String>()

        props.put("name",   getName(property))
        props.put("value",  getValue(property))

        props
    }

    private void renderEnumEditor(GrailsDomainClassProperty property, ConstrainedProperty cp) {
        Map props = createPropertiesMap(property)
        props.put("from",   "${property.type.name}?.values()")
        props.put("keys",   "${property.type.name}.values()*.name()}")

        (isOptional(cp)) ?: props.put("required", "required")
        renderNoSelection(property, cp, props)

        builder."g:select"(props)
    }

    private void renderStringEditor(GrailsDomainClassProperty property, ConstrainedProperty cp) {
        if (!cp) {
            builder.input(type: "text", name: getName(property), value: getValue(property))
        }
        else {
            Map props = createPropertiesMap(property)

            if ("textarea" == cp.widget || (cp.maxSize >= 25 && !cp.password && !cp.inList)) {
                props.remove("value")
                props.put("cols", "40")
                props.put("rows", Math.min((cp.maxSize/25).intValue(), 5))

                (!cp.maxSize)        ?: props.put("maxlength",   cp.maxSize)
                (isOptional(cp))    ?: props.put("required",    "required")

                builder.textarea(props, getValue(property))
            }
            else if (cp.inList) {
                props.put("from",               "\${${getResult()}.constraints.${property.name}.inList}")
                props.put("valueMessagePrefix", getValue(property))

                (isOptional(cp)) ?: props.put("required", "required")
                renderNoSelection(property, cp, props)

                builder."g:select"(props)
            }
            else {
                (!cp.maxSize)       ?: props.put("maxlength",   cp.maxSize)
                (cp.editable)       ?: props.put("readonly",    "readonly")
                (!cp.matches)       ?: props.put("pattern",     "\${${getResult()}.constraints.${property.name}.matches}")
                (isOptional(cp))   ?: props.put("required",    "required")

                if (cp.password) {
                    props.put("type", "password")
                }
                else if (cp.url) {
                    props.put("type", "url")
                }
                else if (cp.email) {
                    props.put("type", "email")
                }
                else {
                    props.put("type", "text")
                }

                builder.input(props)
            }
        }
    }

    private void renderByteArrayEditor(GrailsDomainClassProperty property) {
        builder.input(type: "file", id: property.name, name: getName(property))
    }

    private void renderManyToOne(GrailsDomainClassProperty property, ConstrainedProperty cp) {
        if (property.association) {
            Map props = createPropertiesMap(property)
            props.put("value",      "\${${getValueReady(property)}?.id}")
            props.put("id",         property.name)
            props.put("from",       "\${${property.type.name}.list()}")
            props.put("optionKey",  "id")
            props.put("class",      "many-to-one")

            (isOptional(cp)) ?: props.put("required", "required")
            renderNoSelection(property, cp, props)

            builder."g:select"(props)
        }
    }

    private void renderManyToMany(GrailsDomainClassProperty property, ConstrainedProperty cp) {
        Class cls = property.referencedDomainClass?.clazz

        if (cls == null) {
            if (property.type instanceof Collection) {
                cls = GenericCollectionTypeResolver.getCollectionType(property.type)
            }
        }

        if (cls != null) {
            Map props = createPropertiesMap(property)
            props.put("value",      "\${${getValueReady(property)}?.id}")
            props.put("from",       "\${${cls.name}.list()}")
            props.put("multiple",   "multiple")
            props.put("optionKey",  "id")
            props.put("size",       "5")
            props.put("class",      "many-to-many")

            (isOptional(cp)) ?: props.put("required", "required")

            builder."g:select"(props)
        }
    }

    private void renderOneToMany(GrailsDomainClassProperty property) {
        builder.ul(class: "one-to-many") {
            builder."g:each"(in: "\${${getResult()}.${property.name}?}", var: "instance") {
                builder.li {
                    builder.a(href: "../${property.referencedDomainClass.propertyName}/show/\${instance.id}", "\${instance?.encodeAsHTML()}")
                }
            }
            builder.li(class: "add") {
                builder."g:link"(controller: "${property.referencedDomainClass.propertyName}", action: "create", params: "['${domainClass.propertyName}.id': ${getResult}?.id]") {
                    builder."g:message"(code: "default.add.label", args: "[g.message(code: \"${property.referencedDomainClass.propertyName}.label)\"]")
                }
            }
        }
    }

    private void renderNumberEditor(GrailsDomainClassProperty property, ConstrainedProperty cp) {
        if (!cp) {
            if (property.type == Byte) {
                builder."g:select"(name: getName(property), from: "\${-128..127}", class: "range", value: getValue(property))
            }
            else {
                builder."g:field"(type: "number", name: getName(property), value: getValue(property))
            }
        }
        else {
            Map props = createPropertiesMap(property)

            (isOptional(cp)) ?: props.put("required", "required")
            renderNoSelection(property, cp, props)

            if (cp.range) {
                props.put("from",   "\${${cp.range.from}..${cp.range.to}}")
                props.put("class",  "range")

                builder."g:select"(props)
            }
            else if (cp.inList) {
                props.put("from",               "\${${getResult()}.constraints.${property.name}.inList}")
                props.put("valueMessagePrefix", getValue(property))

                builder."g:select"(props)
            }
            else {
                props.put("type",   "number")

                (!cp.scale)  ?: props.put("step",    BigDecimal.valueOf(1).movePointLeft(cp.scale).toString())
                (!cp.min)    ?: props.put("min",     cp.min)
                (!cp.max)    ?: props.put("max",     cp.max)

                builder."g:field"(props)
            }
        }
     }

    private void renderBooleanEditor(GrailsDomainClassProperty property, ConstrainedProperty cp) {
        if (!cp) {
            builder.input(type: "checkbox", name: getName(property), value: getValue(property))
        }
        else {
            Map props = createPropertiesMap(property)

            (!cp.widget) ?: props.put("widget", cp.widget)

            cp.attributes.each { k, v ->
                props.put(k, v)
            }

            builder."g:checkBox"(props)
        }
    }

    private void renderDateEditor(GrailsDomainClassProperty property, ConstrainedProperty cp) {
        String precision = (property.type == Date || property.type == java.sql.Date || property.type == Calendar) ? "day" : "minute"

        if (!cp) {
            builder."g:datePicker"(name: getName(property), precision: precision, value: getValue(property))
        }
        else {
            if (!cp.editable) {
                builder.span(getValue(property))
            }
            else {
                Map props = createPropertiesMap(property)
                props.put("type",   "text")
                props.put("class",  "datepicker")

                (isOptional(cp)) ?: props.put("required", "required")

                builder.input(props)
            }
        }
    }

    private void renderSelectTypeEditor(String type, GrailsDomainClassProperty property, ConstrainedProperty cp) {
        if (!cp) {
            builder."g:${type}Select"(name: getName(property), value: getValue(property))
        }
        else {
            Map props = createPropertiesMap(property)

            (!cp.widget) ?: props.put("widget", cp.widget)
            renderNoSelection(property, cp, props)

            cp.attributes.each { k, v ->
                props.put(k, v)
            }

            builder."g:${type}Select"(props)
        }
    }

    private static void renderNoSelection(GrailsDomainClassProperty property, ConstrainedProperty cp, Map<String, String> map) {
        if (isOptional(cp)) {
            if (property.manyToOne || property.oneToOne) {
                map.put("noSelection",  "['null': '']")
            }
            else if (property.type == Date || property.type == java.sql.Date || property.type == java.sql.Time || property.type == Calendar) {
                map.put("default",      "none")
                map.put("noSelection",  "['': '']")
            }
            else {
                map.put("noSelection",  "['': '']")
            }
        }
    }

    private static boolean isRequired(ConstrainedProperty cp) {
        !isOptional(cp)
    }

    private static boolean isOptional(ConstrainedProperty cp) {
        if (!cp) {
            return false
        }
        else {
            cp.nullable || (cp.propertyType == String && cp.blank) || cp.propertyType in [boolean, Boolean]
        }
    }
}
