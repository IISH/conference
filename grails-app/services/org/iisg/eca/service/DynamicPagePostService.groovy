package org.iisg.eca.service

import org.iisg.eca.dynamic.Column
import org.iisg.eca.dynamic.DataContainer
import org.iisg.eca.dynamic.DynamicPageResults

import org.codehaus.groovy.grails.web.metaclass.BindDynamicMethod
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.commons.metaclass.GroovyDynamicMethodsInterceptor

/**
 * Service responsible for saving the data of a form on a dynamic page
 */
class DynamicPagePostService {
    /**
     * Make sure we can use the bind method within all methods of this service
     */
    DynamicPagePostService() {
        GroovyDynamicMethodsInterceptor i = new GroovyDynamicMethodsInterceptor(this)
        i.addDynamicMethodInvocation(new BindDynamicMethod())
    }

    /**
     * Saves the data of the given element from a dynamic page
     * @param element The element, which should represent a form, stating the data and the relationships between columns
     * @param params The data filled out by the user to be saved in the database
     * @return A list of domain objects which were saved to the database
     */
    DynamicPageResults saveFormData(DataContainer element, GrailsParameterMap params) {
        // This element should be a form...
        if (element?.type == DataContainer.Type.FORM) {
            DynamicPageResults results = new DynamicPageResults(element, params)
            Set<Column> hasChildren = new HashSet<Column>()

            // Loop over all domain classes, binding the data from ONLY those columns specified in the dynamic page
            element.allDomainClasses.each { domainClass ->
                Set<Column> columns = element.getAllColumnsForDomainClass(domainClass)

                // If there is only one instance, just save that one
                if (columns.findAll { (it.parent instanceof Column) && (it.parent.multiple) }.isEmpty()) {
                    bindData(results.get(domainClass.name), params, [include: columns.collect { it.name }], domainClass.name)
                }
                else {
                    // First delete the records that are to be deleted
                    String[] ids = params["${domainClass.name}.to-be-deleted"].split(';')
                    ids.each { idToDelete ->
                        if (idToDelete.isLong()) {
                            // See if there is a soft delete, otherwise perform a hard delete
                            if (domainClass.clazz.hasProperty('deleted')) {
                                Object domain = domainClass.clazz.get(idToDelete.toLong())
                                domain?.softDelete()
                                domain?.save()
                            }
                            else {
                                domainClass.clazz.get(idToDelete.toLong())?.delete()
                            }
                        }
                    }

                    // Every new instance has a new number which simply adds up and the domain class name as a prefix
                    // Keep on saving every instance, until there are no values in the parameters map anymore with the given prefix
                    int i = 0
                    while (params."${domainClass.name}_${i}") {
                        Long id = params.long("${domainClass.name}_${i}.id")
                        if (!ids.find { it.isLong() && (id == it.toLong()) }) {
                            bindData(results.get(domainClass.name, id), params, [include: columns.collect { it.name }], "${domainClass.name}_${i}".toString())
                            i++
                        }
                    }
                }

                // Save those columns with children for later, we need to combine the results later on
                hasChildren.addAll(columns.grep { it.hasElements() })
            }         
            
            // For all those results, create a new list, so we can filter out which objects need to be saved
            List<Object> resultsToSave = results.get() as List

            // Explore the relationships of every column and combine the results
            hasChildren.each { c ->
                Object[] owningSide = results.getArray(c.domainClass.name)
                Object[] otherSide = results.getArray(c.property.referencedDomainClass.name)
                GrailsDomainClassProperty owner = c.property

                // Make sure the owning side actually is the owning side, if not, switch them around
                if (!c.property.owningSide) {
                    owningSide = otherSide
                    otherSide = results.getArray(c.domainClass.name)
                    owner = c.property.otherSide
                }

                // Add the other side to the owning side, either to a collection or not
                if (owner.oneToOne || owner.manyToOne) {
                    owningSide.each { dc ->
                        otherSide.each { os ->
                            dc[owner.name] = os
                        }
                    }
                }
                else if (owner.manyToMany || owner.oneToMany) {
                    owningSide.each { dc ->
                        otherSide.each { os ->
                            dc."addTo${owner.name[0].toUpperCase() + owner.name[1..-1]}"(os)
                        }
                    }
                }

                // The owning side needs to be saved and takes care of the relationship
                // So remove the other side from the list of results to save
                otherSide.each { resultsToSave.remove(it) }
            }

            // We're done, save the results that have to be saved
            resultsToSave.each { it.save() }

            return results
        }
        
        return null
    }
}
