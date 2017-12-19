package org.iisg.eca.dynamic

import groovy.transform.CompileStatic

/**
 * An element of a dynamic page
 */
@CompileStatic
abstract class Element {
    private String name
    private ContainerElement parent     
    
    /**
     * Creates a new element
     * @param name The name of the new element
     */
    Element(String name) {
        this.name = name
        this.parent = null
    }
    
    /**
     * A getter method which returns the name of this element
     * @returns The name of this element
     */
    String getName() {
        name
    }
    
    /**
     * A getter method which returns the parent of this element
     * @returns The parent of this element
     */
    ContainerElement getParent() {
        parent
    }
    
    /**
     * Returns the root element
     * @returns The root of this element
     */
    ContainerElement getRoot() {
        ContainerElement current = this.parent
        while (current?.parent) {
            current = current.parent  
        }
        current
    }
    
    /**
     * Sets the parent of this element
     * @param parent The parent element of this element
     */
    void setParent(ContainerElement parent) {
        this.parent = parent  
    }
    
    /**
     * Returns all of the elements on the way to the root
     * @returns A list of all the elements on the way to the root
     */
    List<Element> getPath() {
        List<Element> path = new ArrayList<Element>()
        Element current = this

        while (current) {
            path.add(0, current)
            current = current.parent
        }

        path
    }

    @Override
    String toString() {
        name
    }
}
