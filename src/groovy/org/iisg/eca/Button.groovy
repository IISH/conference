package org.iisg.eca

class Button extends Element {
    private ButtonType buttonType
    private String controller
    private String action
    private String id
    
    /**
     * The possible button types
     */
    enum ButtonType {
        SAVE, BACK, URL
    }
    
    Button(ButtonType buttonType, String name) {
        super(name, Element.ElementType.BUTTON)
        this.buttonType = buttonType

        this.controller = "\${params.controller}"
        this.action = "\${params.action}"
        this.id = "\${params.id}"
    }
    
    ButtonType getButtonType() {
        return buttonType    
    }

    String getController() {
        controller
    }

    String getAction() {
        action
    }

    String getId() {
        id
    }

    void setController(String controller) {
        if (controller && !controller.isEmpty()) {
            this.controller = controller
        }
    }

    void setAction(String action) {
        if (action && !action.isEmpty()) {
            this.action = action
        }
    }

    void setId(String id) {
        if (id && !id.isEmpty()) {
            this.id = id
        }
    }
}
