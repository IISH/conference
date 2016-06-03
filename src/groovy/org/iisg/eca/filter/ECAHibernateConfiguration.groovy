package org.iisg.eca.filter

import org.iisg.eca.domain.EventDomain
import org.iisg.eca.domain.EventDateDomain

import org.hibernate.MappingException
import org.hibernate.mapping.Filterable
import org.hibernate.engine.spi.FilterDefinition

import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty

import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsAnnotationConfiguration

/**
 * Extends the Hibernate configuration with Hibernate filters
 */
class ECAHibernateConfiguration extends GrailsAnnotationConfiguration {
    private GrailsApplication grailsApplication

    @Override
    public void setGrailsApplication(GrailsApplication grailsApplication) {
        super.setGrailsApplication(grailsApplication)
        this.grailsApplication = grailsApplication
    }

    @Override
    protected void secondPassCompile() throws MappingException {
        super.secondPassCompile()

        addFilterDefinition(getSoftDeleteFilterDefinition())
        addFilterDefinition(getEventFilterDefinition())
        addFilterDefinition(getEventDateFilterDefinition())

        for (GrailsClass grailsClass : grailsApplication.domainClasses) {
            Class domain = grailsClass.clazz
            DefaultGrailsDomainClass domainClass = (DefaultGrailsDomainClass) grailsClass

            applyForDomainClass(domain, getClassMapping(domain.getName()))
            for (GrailsDomainClassProperty column : domainClass.associations) {
                // Only possible on collections (oneToMany, manyToMany)
                // TODO: manyToMany fails: filter is applied to join table, resulting in invalid SQL
                if (column.oneToMany) {
                    applyForDomainClass(
                            column.referencedPropertyType,
                            getCollectionMapping("${domain.getName()}.${column.name}")
                    )
                }
            }
        }
    }

    private FilterDefinition getSoftDeleteFilterDefinition() {
        return new FilterDefinition('softDeleteFilter', 'deleted = 0', [:])
    }

    private FilterDefinition getEventFilterDefinition() {
        return new FilterDefinition('eventFilter', '(event_id = :eventId OR event_id IS NULL)',
                ['eventId': typeResolver.basic('long')])
    }

    private FilterDefinition getEventDateFilterDefinition() {
        new FilterDefinition('eventDateFilter', '(date_id = :dateId OR date_id IS NULL)',
                ['dateId': typeResolver.basic('long')])
    }

    private void applyForDomainClass(Class domain, Filterable entity) {
        if (domain.isAnnotationPresent(SoftDelete)) {
            addFilterToEntity(entity, getSoftDeleteFilterDefinition())
        }

        if (EventDomain.class.isAssignableFrom(domain)) {
            addFilterToEntity(entity, getEventFilterDefinition())
        }
        else if (EventDateDomain.class.isAssignableFrom(domain)) {
            addFilterToEntity(entity, getEventDateFilterDefinition())
        }
    }

    private static void addFilterToEntity(Filterable entity, FilterDefinition filterDefinition) {
        entity.addFilter(filterDefinition.getFilterName(), filterDefinition.getDefaultFilterCondition(), true, [:], [:])
    }
}