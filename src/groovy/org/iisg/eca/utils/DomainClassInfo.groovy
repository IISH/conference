package org.iisg.eca.utils

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty

class DomainClassInfo {
    private GrailsDomainClass domainClass

    DomainClassInfo(GrailsDomainClass domainClass) {
        this.domainClass = domainClass
    }

    DomainClassInfo(GrailsApplication grailsApplication, String domainClass) {
        this.domainClass = (GrailsDomainClass) grailsApplication.domainClasses.find { it.clazz.simpleName == domainClass }
    }

    boolean foundDomainClass() {
        return this.domainClass != null
    }

    Class getDomainClass() {
        return domainClass.clazz
    }

    boolean hasProperty(String property) {
        domainClass.hasProperty(property)
    }

    String getTypeNameForProperty(String property) {
        GrailsDomainClassProperty dcProperty = this.getPropertyByName(property)
        dcProperty.getTypePropertyName()
    }

    String getTypeForProperty(String property) {
        GrailsDomainClassProperty dcProperty = this.getPropertyByName(property)
        dcProperty.getType()
    }

    DomainClassInfo getDomainClassInfoForProperty(String property) {
        GrailsDomainClassProperty dcProperty = this.getPropertyByName(property)
        return new DomainClassInfo(dcProperty.referencedDomainClass)
    }

    Object convertValueForProperty(String property, String value) {
        String type = this.getTypeNameForProperty(property)
        switch (type) {
            case 'int':
            case 'integer':
                return value?.isInteger() ? value.toInteger() : null
            case 'long':
                return value?.isLong() ? value.toLong() : null
            case 'boolean':
                return value?.equals('1') || value?.equalsIgnoreCase('true')
            case 'date':
                return value?.isLong() ? new Date(value.toLong() * 1000) : null
            default:
                return value
        }
    }

    private GrailsDomainClassProperty getPropertyByName(String property) {
        return this.domainClass.properties.find { it.name == property }
    }
}
