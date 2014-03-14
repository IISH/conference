package org.iisg.eca.utils

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.validation.Constraint
import org.codehaus.groovy.grails.validation.NullableConstraint

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

	Constraint getConstraintForProperty(String property, String constraint) {
		return this.domainClass.constrainedProperties?.get(property)?.getAppliedConstraint(constraint)
	}

	boolean propertyAllowsNull(String property) {
		NullableConstraint nullableConstraint = (NullableConstraint) getConstraintForProperty(property, 'nullable')
		return nullableConstraint?.isNullable()
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
	        case 'string':
		        if ((value == null) || value.trim().isEmpty()) {
			        return this.propertyAllowsNull(property) ? null : ''
		        }
		        return value.trim()
            default:
                return value
        }
    }

    private GrailsDomainClassProperty getPropertyByName(String property) {
        return this.domainClass.properties.find { it.name == property }
    }
}
