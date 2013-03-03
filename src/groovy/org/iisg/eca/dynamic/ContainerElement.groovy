package org.iisg.eca.dynamic

/**
 * An element which contains other elements
 */
class ContainerElement extends Element {
    private List<Element> elements
    
    /**
     * Creates a new <code>ContainerElement</code>
     * @param name The name of this container element
     * @param elements A list of all elements this element contains
     */
    ContainerElement(String name, List<Element> elements) {
        super(name)
        setElements(elements)
    }
    
    /**
     * Returns all elements this element contains
     * @return A list of elements
     */ 
    List<Element> getElements() {
        elements
    } 
    
    /**
     * Sets all elements this element contains
     * @param elements The elements this element contains
     */ 
    void setElements(List<Element> elements) {
        this.elements = elements
        this.elements*.parent = this
    } 
    
    /**
     * Informs whether this element contains other elements
     * @returns Whether this element contains other elements
     */ 
    boolean hasElements() {
        !elements.isEmpty()
    } 
    
    /**
     * Returns an element this element contains by name
     * @param name The name of the element
     * @return The element with the given name
     */  
    Element getElement(String name) {
        elements.find { it.name.equalsIgnoreCase(name) }
    }
    
    /**
     * Adds an element to the container
     * @param element The element to add
     */  
    void addElement(Element element) {
        this.elements.add(element)
        element.parent = this
    }    
    
    /**
     * Returns all columns this element contains
     * @return A list of columns
     */
    List<Column> getColumns() {
        elements.findAll { it instanceof Column } as List<Column>
    }
    
    /**
     * Informs whether this element contains other columns
     * @returns Whether this element contains other columns
     */ 
    boolean hasColumns() {
        !columns.isEmpty()
    }

    /**
     * Returns all of the columns
     * @return A list of columns
     */
    List<Column> getAllColumns() {
        List<Column> columns = new ArrayList<Column>()
        forAllColumns {
            columns.add(it)
        }
        columns
    }
    
    /**
     * Returns a column this element contains by name
     * @param name The name of the column
     * @return The column with the given name
     */  
    Column getColumn(String name) {
        (Column) elements.find { (it instanceof Column) && (it.name.equalsIgnoreCase(name)) }
    }
    
    /**
     * Returns a column this element hierarchy contains by name
     * @param name The name of the column
     * @return The column with the given name
     */  
    Column getColumnInHierarchy(String name) {
        Column column = null
        forAllColumns { c -> 
            if (c.name.equalsIgnoreCase(name)) {
                column = c
            }
        }
        column
    }
    
    /**
     * Helper method to loop over all columns of this container, 
     * as columns can contain other columns as well
     * @param callable A closure to call for every single column of this container
     */
    void forAllColumns(Closure callable) {
        columns.each { c -> 
            callable(c)            
            forAllColumnsFrom(c, callable)
        }
    }
    
    /**
     * Static helper method to loop over all columns starting from the given column
     * @param column The column to start with
     * @param callable A closure to call for every single column the given column contains
     */
    static void forAllColumnsFrom(Column column, Closure callable) {
        column.columns.each { c ->
            callable(c)            
            forAllColumnsFrom(c, callable)
        }
    }
    
    /**
     * Helper method to loop over all columns of this container with children
     * @param callable A closure to call for every single column of this container with children
     */
    void forAllColumnsWithChildren(Closure callable) {
        columns.each { c -> 
            if (c.hasElements()) {
                callable(c)            
                forAllColumnsWithChildrenFrom(c, callable)
            }
        }
    }
    
    /**
     * Static helper method to loop over all columns with children starting from the given column
     * @param column The column to start with
     * @param callable A closure to call for every single column with children the given column contains
     */
    static void forAllColumnsWithChildrenFrom(Column column, Closure callable) {
        column.columns.each { c ->
            if (c.hasElements()) {
                callable(c)            
                forAllColumnsWithChildrenFrom(c, callable)
            }
        }
    }  
    
    /**
     * Returns all buttons this element contains
     * @return A list of buttons
     */
    List<Button> getButtons() {
        elements.findAll { it instanceof Button } as List<Button>
    }
    
    /**
     * Returns a button this element contains by name
     * @param name The name of the button
     * @return The button with the given name
     */   
    Button getButton(String name) {
        (Button) elements.find { (it instanceof Button) && (it.name.equalsIgnoreCase(name)) }
    }
    
    /**
     * Informs whether this element contains other buttons
     * @returns Whether this element contains other buttons
     */ 
    boolean hasButtons() {
        !buttons.isEmpty()
    } 
       
    /**
     * Returns a list of button this element contains by type
     * @param type The type of the button
     * @return A list of buttons with the given type
     */  
    List<Button> getButtonsByType(Button.Type type) {
        elements.findAll { (it instanceof Button) && (it.buttonType == type) } as List<Button>
    }
}

