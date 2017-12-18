package org.iisg.eca.filter

import org.iisg.eca.domain.EventDomain
import org.iisg.eca.domain.EventDateDomain

import org.hibernate.MappingException
import org.hibernate.mapping.Filterable
import org.hibernate.mapping.Collection
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

    private FilterDefinition softDeleteFilterDefinition
    private FilterDefinition eventFilterDefinition
    private FilterDefinition eventDateFilterDefinition

    @Override
    public void setGrailsApplication(GrailsApplication grailsApplication) {
        super.setGrailsApplication(grailsApplication)
        this.grailsApplication = grailsApplication
    }

    @Override
    protected void secondPassCompile() throws MappingException {
        super.secondPassCompile()
        setFilterDefinitions()
        applyFilters()
    }

    private void setFilterDefinitions() {
        softDeleteFilterDefinition = new FilterDefinition('softDeleteFilter', 'deleted = 0', [:])
        addFilterDefinition(softDeleteFilterDefinition)

        eventFilterDefinition = new FilterDefinition('eventFilter', '(event_id = :eventId OR event_id IS NULL)',
                ['eventId': typeResolver.basic('long')])
        addFilterDefinition(eventFilterDefinition)

        eventDateFilterDefinition = new FilterDefinition('eventDateFilter', '(date_id = :dateId OR date_id IS NULL)',
                ['dateId': typeResolver.basic('long')])
        addFilterDefinition(eventDateFilterDefinition)
    }

    private void applyFilters() {
        for (GrailsClass grailsClass : grailsApplication.domainClasses) {
            Class domain = grailsClass.clazz
            DefaultGrailsDomainClass domainClass = (DefaultGrailsDomainClass) grailsClass

            applyForDomainClass(domain, getClassMapping(domain.getName()), false)
            for (GrailsDomainClassProperty column : domainClass.associations) {
                if (column.oneToMany || column.manyToMany) {
                    Filterable collectionMapping = getCollectionMapping("${domain.getName()}.${column.name}")
                    applyForDomainClass(column.referencedPropertyType, collectionMapping, column.manyToMany)
                }
            }
        }
    }

    private void applyForDomainClass(Class domain, Filterable entity, boolean isManyToMany) {
        if (domain.isAnnotationPresent(SoftDelete))
            addFilterToEntity(entity, softDeleteFilterDefinition, isManyToMany)

        if (EventDomain.class.isAssignableFrom(domain))
            addFilterToEntity(entity, eventFilterDefinition, isManyToMany)
        else if (EventDateDomain.class.isAssignableFrom(domain))
            addFilterToEntity(entity, eventDateFilterDefinition, isManyToMany)
    }

    private static void addFilterToEntity(Filterable entity, FilterDefinition filterDefinition, boolean isManyToMany) {
        if (isManyToMany && (entity instanceof Collection))
            entity.addManyToManyFilter(filterDefinition.getFilterName(), filterDefinition.getDefaultFilterCondition(), true, [:], [:])
        else
            entity.addFilter(filterDefinition.getFilterName(), filterDefinition.getDefaultFilterCondition(), true, [:], [:])
    }
}