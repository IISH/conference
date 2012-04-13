package org.iisg.eca

import groovy.util.slurpersupport.GPathResult

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

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

    private static final String DOMAIN_PACKAGE = "org.iisg.eca"
    
    private static final String FORM =      'form'
    private static final String TABLE =     'table'
    private static final String OVERVIEW =  'overview'
    private static final String BUTTONS =   'buttons'
    
    private static final String COLUMN =    'column'
    private static final String BUTTON =    'button'

    /**
     * Returns the dynamic page with all described elements if there is one available
     */
    def getDynamicPage() {
        // Find out if there is are dynamic pages linked to this page
        int noDynamicPages = pageInformation.page.dynamicPages.size()
        if (noDynamicPages == 1) {
            // Parse the xml content for the elements
            DynamicPage dynamicPage = pageInformation.page.dynamicPages.toArray()[0]
            dynamicPage.elements = gePageElements(dynamicPage.xml)

            return dynamicPage
        }

        return null
    }

    /**
     * Returns a template of the dynamic page for the current request
     * @param dynamicPage A dynamic page with the described elements
     * @param params The parameters of the current request
     * @return A template of the page with all the results
     */
    def getTemplate(DynamicPage dynamicPage, GrailsParameterMap params) {
        getTemplate(dynamicPage, params, null)
    }

    /**
     * Returns a template of the dynamic page for the current request
     * with the results of a particular element on the page
     * @param dynamicPage A dynamic page with the described elements
     * @param params The parameters of the current request
     * @param results The result of a particular element on the page
     * @return A template of the page with all the results
     */
    def getTemplate(DynamicPage dynamicPage, GrailsParameterMap params, DynamicPageResults results) {
        // If there is no cache found in the database, create it now
        if (!dynamicPage.cache) {
            PageBuilder builder = new PageBuilder(dynamicPage.elements)
            dynamicPage.cache = builder.buildPage()
            dynamicPage.save()
        }

        // Use the cache to fill the elements on the page with the requested data from the database
        def tmpl = groovyPagesTemplateEngine.createTemplate(dynamicPage.cache, 'temp')
        StringWriter out = new StringWriter()

        // Get all the results; overwrite the results of the element send along if present
        Map<Integer, DynamicPageResults> allResults = dynamicPage.getResults(params)
        if (results) {
            allResults.put(params.int('eid'), results)
        }

        tmpl.make([page: dynamicPage, results: allResults]).writeTo(out)
        out.toString()
    }

    /**
     * Parses the xml describing the dynamic page in search of page elements
     * @param xml The xml representing the dynamic page
     * @return A map of all page elements of the dynamic page linked to their element id
     */
    private Map<Integer, PageElement> gePageElements(GPathResult xml) {
        Map<Integer, PageElement> pageElements = new HashMap<Integer, PageElement>()
        int eid = 0
                
        // Loop over all the elements on the page
        xml.children().each { xmlElement ->
            String elemType = xmlElement.name()

            if (elemType == FORM || elemType == TABLE || elemType == OVERVIEW) {
                String domain = xmlElement.@domain.text()             
                if (!domain.isEmpty()) {
                    GrailsDomainClass domainClass = grailsApplication.getDomainClass("${DOMAIN_PACKAGE}.${domain}")
                
                    PageElement.Type type
                    switch (elemType) {
                        case FORM:
                            type = PageElement.Type.FORM
                            break
                        case TABLE:
                            type = PageElement.Type.TABLE
                            break
                        case OVERVIEW:
                            type = PageElement.Type.OVERVIEW  
                    }

                    List<Element> elements = inspectElement(xmlElement, domainClass)   

                    if (!elements.isEmpty()) {
                        ViewElement pageElement = new ViewElement(eid++, type, domainClass, elements)
                        pageElement.id = xmlElement.@id.text()
                        pageElement.query = xmlElement.@query.text()

                        pageElements.put(pageElement.eid, pageElement)
                    }
                }
            }
            else if (elemType == BUTTONS) {
                List<Element> elements = inspectElement(xmlElement, null)
                
                if (!elements.isEmpty()) {
                    PageElement pageElement = new ButtonElement(eid++, elements)
                    
                    pageElements.put(pageElement.eid, pageElement)
                }
            }
        }
        
        pageElements
    }
    
    private List<Element> inspectElement(GPathResult xmlElement, GrailsDomainClass domainClass) {
        List<Element> elements = new ArrayList<Element>() 

        xmlElement.children().each { element ->
            String type = element.name() 
            if (type == COLUMN) {  
                String name = element.@name.text()
                String domain = element.@domain.text()
                if (!domain.isEmpty()) {
                    domainClass = grailsApplication.getDomainClass("${DOMAIN_PACKAGE}.${domain}")
                }

                List<Column> children = inspectElement(element, domainClass)
                Column column = new Column(name, domainClass, children)

                boolean readOnly = element.@readonly.text().equalsIgnoreCase("true")
                boolean multiple = element.@multiple.text().equalsIgnoreCase("true")

                column.readOnly = readOnly
                column.multiple = multiple

                elements.add(column)
            }  
            else if (type == BUTTON) {
                String typeName = element.@type.text()
                
                Button.ButtonType buttonType
                switch (typeName.toLowerCase()) {
                    case 'back':
                    case 'cancel':
                        buttonType = Button.ButtonType.BACK
                        break
                    case 'save':
                        buttonType = Button.ButtonType.SAVE
                        break
                    default:
                        buttonType = Button.ButtonType.URL
                }

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
