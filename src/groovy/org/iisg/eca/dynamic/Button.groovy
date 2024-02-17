package org.iisg.eca.dynamic

/**
 * An element which represents a button on a dynamic page
 */
class Button extends Element {
    private Type type
    private String controller
    private String action
    private String id
    
    /**
     * The possible button types
     * SAVE: A save button to send the information filled out in a form to the server
     * BACK: A back button to cancel the current request and return to the previous page
     * DELETE: A delete button pointing to the delete action of the same controller
     * URL: A button taking the user to another page
     */
    enum Type {
        SAVE, BACK, DELETE, URL
    }
    
    /**
     * Creates a new <code>Button</code> element
     * @param type The type of this button
     * @param name The name of this button
     */
    Button(Type type, String name) {
        super(name)
        this.type = type

        // Use the controller, action and id of the dynamic page
        this.controller = "\${params.controller}"
        this.action = "\${params.action}"
        this.id = "\${params.id}"
    }
    
    /**
     * Returns the type of this button
     * @returns The button type
     */
    Type getType() {
        return type    
    }

    /**
     * Returns the controller this button links to
     * @returns The name of the controller
     */
    String getController() {
        controller
    }

    /**
     * Returns the action this button links to
     * @returns The name of the action
     */
    String getAction() {
        action
    }

    /**
     * Returns the id this button links to
     * @returns The id
     */
    String getId() {
        id
    }

    /**
     * Sets the controller this button links to
     * @params controller The name of the controller
     */
    void setController(String controller) {
        if (controller && !controller.isEmpty()) {
            this.controller = controller
        }
    }

    /**
     * Sets the action this button links to
     * @params action The name of the action
     */
    void setAction(String action) {
        if (action && !action.isEmpty()) {
            this.action = action
        }
    }

    /**
     * Sets the id this button links to
     * @params id The name of the id
     */
    void setId(String id) {
        if (id && !id.isEmpty()) {
            this.id = id
        }
    }
}
