package org.iisg.eca.service

import org.iisg.eca.dynamic.DataContainer
import org.iisg.eca.dynamic.Button
import org.iisg.eca.dynamic.Element
import org.iisg.eca.dynamic.PageBuilder
import org.iisg.eca.dynamic.DynamicPageResults
import org.iisg.eca.dynamic.ContainerElement
import org.iisg.eca.dynamic.Column

import org.iisg.eca.domain.DynamicPage

import groovy.util.slurpersupport.GPathResult

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import groovy.text.Template
import org.iisg.eca.domain.Page

/**
 * Service responsible for dynamic page related actions
 */
class DynamicPageService {
    /**
     * grailsApplication gives us the possibility to search for specific domain classes
     */
    def grailsApplication
    
    /**
     * groovyPagesTemmplateEngine allows us to render the dynamic pages
     */
    def groovyPagesTemplateEngine

    /**
     * pageInformation gives us information about the current page
     */
    def pageInformation

    private static final String DOMAIN_PACKAGE = "org.iisg.eca.domain"
    
    private static final String FORM =      'form'
    private static final String TABLE =     'table'
    private static final String OVERVIEW =  'overview'
    private static final String BUTTONS =   'buttons'
    
    private static final String COLUMN =    'column'
    private static final String BUTTON =    'button'

    /**
     * Returns the dynamic page with all described elements, if there is one available
     */
    DynamicPage getDynamicPage() {
        getDynamicPage(pageInformation.page)
    }

    /**
     * Returns the dynamic page for a specific page with all described elements, if there is one available
     */
    DynamicPage getDynamicPage(Page page) {
        // Get the dynamic page linked to this page
        DynamicPage dynamicPage = (DynamicPage) DynamicPage.getByDate(page.dynamicPages as List)

        // Parse the xml content for the elements
        if (dynamicPage) {
            dynamicPage.elements = gePageElements(dynamicPage.xml)
        }

        dynamicPage
    }

    /**
     * Returns a template of the dynamic page for the current request
     * with the results of a particular element on the page
     * @param dynamicPage A dynamic page with the described elements
     * @param params The parameters of the current request
     * @param results The results to display
     * @return A template of the page with all the results
     */
    def getTemplate(DynamicPage dynamicPage, GrailsParameterMap params, Map<Integer, DynamicPageResults> results)  {
        // If there is no cache found in the database, create it now
        if (!dynamicPage.cache) {
            PageBuilder builder = new PageBuilder(dynamicPage.elements)
            dynamicPage.cache = builder.buildPage()
            dynamicPage.internalUpdate = true
            dynamicPage.save()
        }

        // Use the cache to fill the elements on the page with the requested data from the database
        Template tmpl = groovyPagesTemplateEngine.createTemplate(dynamicPage.cache, 'temp')
        StringWriter out = new StringWriter()

        tmpl.make([page: dynamicPage, results: results]).writeTo(out)
        out.toString()
    }

    /**
     * Parses the xml describing the dynamic page in search of page elements
     * @param xml The xml representing the dynamic page
     * @return A map of all page elements of the dynamic page linked to their element id
     */
    private List<ContainerElement> gePageElements(GPathResult xml) {
        List<ContainerElement> pageElements = new ArrayList<ContainerElement>()
        int eid = 0
                
        // Loop over all the elements on the page
        xml.children().each { xmlElement ->
            String elemType = xmlElement.name()

            // Find out if the element will hold data or buttons, if neither: ignore
            if (elemType == FORM || elemType == TABLE || elemType == OVERVIEW) {
                String domain = xmlElement.@domain.text()             
                if (!domain.isEmpty()) {
                    GrailsDomainClass domainClass = (GrailsDomainClass) grailsApplication.getDomainClass("${DOMAIN_PACKAGE}.${domain}")
                
                    DataContainer.Type type
                    switch (elemType) {
                        case FORM:
                            type = DataContainer.Type.FORM
                            break
                        case TABLE:
                            type = DataContainer.Type.TABLE
                            break
                        case OVERVIEW:
                            type = DataContainer.Type.OVERVIEW
                    }

                    // Get a list of all elements contained by this element
                    List<Element> elements = inspectElement(xmlElement, domainClass)   

                    // If there are no elements, there is nothing to display either
                    if (!elements.isEmpty()) {
                        DataContainer dataContainer = new DataContainer(eid++, null, type, domainClass, elements)
                        dataContainer.id = xmlElement.@id.text()
                        dataContainer.index = xmlElement.@index.text().equalsIgnoreCase("true")
                        dataContainer.totals = xmlElement.@totals.text().equalsIgnoreCase("true")
                        dataContainer.action = xmlElement.@action.text()

                        pageElements.add(dataContainer)
                    }
                }
            }
            else if (elemType == BUTTONS) {
                List<Element> elements = inspectElement(xmlElement, null)
                
                if (!elements.isEmpty()) {
                    ContainerElement containerElement = new ContainerElement(null, elements)
                    pageElements.add(containerElement)
                }
            }
        }
        
        pageElements
    }

    /**
     * Inspects the elements contained by the given element, which could be columns or buttons,
     * and creates the corresponding element objects for it
     * @param xmlElement The xml representing the element
     * @param domainClass The domain class to which these columns belong
     * @return A list of elements contained by the given element
     */
    private List<Element> inspectElement(GPathResult xmlElement, GrailsDomainClass domainClass) {
        List<Element> elements = new ArrayList<Element>()

        // Loop over all the children and create element objects for every one of them
        xmlElement.children().each { element ->
            String type = element.name() 

            // The element should be either a column or a button
            if (type == COLUMN) {
                GrailsDomainClass curDomainClass = domainClass

                String name = element.@name.text()
                String domain = element.@domain.text()
                if (!domain.isEmpty()) {
                    curDomainClass = (GrailsDomainClass) grailsApplication.getDomainClass("${DOMAIN_PACKAGE}.${domain}")
                }

                // The column might have children as well, so inspect this column as well
                // If the column references another domain class, set that domain class
                GrailsDomainClass refDomain = curDomainClass.getPropertyByName(name)?.referencedDomainClass
                List<Column> children
                if (refDomain) {
                    children = inspectElement(element, refDomain) as List<Column>
                }
                else {
                    children = inspectElement(element, domainClass) as List<Column>
                }

                // Create a new column element object and add the remaining properties
                Column column = new Column(name, curDomainClass, children)
                
                column.id           = element.@id.text().equalsIgnoreCase("true")
                column.readOnly     = element.@readonly.text().equalsIgnoreCase("true")
                column.multiple     = element.@multiple.text().equalsIgnoreCase("true")
                column.hidden       = element.@hidden.text().equalsIgnoreCase("true")
                column.notEmpty     = element.@notEmpty.text().equalsIgnoreCase("true")
                column.filter       = element.@filter.text().equalsIgnoreCase("true")
                column.hideFilter   = element.@hideFilter.text().equalsIgnoreCase("true")
                column.interactive  = element.@interactive.text()
                column.textarea     = element.@textarea.text()
                column.eq           = element.@eq.text()

                elements.add(column)
            }  
            else if (type == BUTTON) {
                String typeName = element.@type.text()
                
                Button.Type buttonType
                switch (typeName.toLowerCase()) {
                    case 'back':
                    case 'cancel':
                        buttonType = Button.Type.BACK
                        break
                    case 'save':
                        buttonType = Button.Type.SAVE
                        break
                    case 'delete':
                        buttonType = Button.Type.DELETE
                        break
                    default:
                        buttonType = Button.Type.URL
                }

                // Create a new button element object and add the remaining properties
                Button button = new Button(buttonType, typeName)
                button.controller = element.@controller.text()
                button.action = element.@action.text()
                button.id = element.@id.text()

                elements.add(button)
            }
        }
        
        elements
    }    
}
