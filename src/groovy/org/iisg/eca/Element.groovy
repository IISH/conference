package org.iisg.eca

abstract class Element {
    private String name
    private ElementType type
    private PageElement pageElement
    
    /**
     * The possible element types
     */
    enum ElementType {
        COLUMN, BUTTON
    }
    
    Element(String name, ElementType type) {
        this.name = name
        this.type = type
        this.pageElement = null
    }
    
    String getName() {
        name
    }
    
    /**
     * Returns the type of this element
     * @return The element type
     */
    ElementType getType() {
        type
    }    
        
    PageElement getPageElement() {
        pageElement
    }
    
    void setPageElement(PageElement pageElement) {
        this.pageElement = pageElement  
    }
}
