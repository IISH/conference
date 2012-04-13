package org.iisg.eca

abstract class PageElement {
    private int eid
    private Type type
    private List<Element> elements
    
    /**
     * The possible element types
     */
    enum Type {
        FORM, TABLE, OVERVIEW, BUTTONS
    }
    
    PageElement(int eid, Type type, List<Element> elements) {
        this.eid = eid
        this.type = type
        this.elements = elements
        this.elements*.pageElement = this
    }
    
    int getEid() {
        eid
    }    
    
    /**
     * Returns the type of this element
     * @return The element type
     */
    Type getType() {
        type
    }    
    
    List<Element> getElements() {
        elements
    }
     
    Element getElementOfType(Element.ElementType type) {
        elements.find { it.type == type }
    }
    
        /**
     * Returns a map of all the columns requested for this element, linked to their domain class
     * @return A map of all the columns
     */
    List<Column> getColumns() {
        elements.grep { it.type == Element.ElementType.COLUMN }
    }
       
    /**
     * Returns a list of all columns requested for this element by the specified domain class name
     * @param domainClass The domain class name in question
     * @return A list of all columns
     */
    Column get(String name) {
        (Column) elements.find { it.type == Element.ElementType.COLUMN && it.name.equalsIgnoreCase(name) }
    }
    
    /**
     * Returns a map of all the columns requested for this element, linked to their domain class
     * @return A map of all the columns
     */
    List<Button> getButtons() {
        elements.grep { it.type == Element.ElementType.BUTTON }
    }
       
    /**
     * Returns a list of all columns requested for this element by the specified domain class name
     * @param domainClass The domain class name in question
     * @return A list of all columns
     */
    List<Button> getButtonsByType(Button.ButtonType type) {
        elements.grep { it.type == Element.ElementType.BUTTON && it.buttonType == type }
    }
}
