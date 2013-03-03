package org.iisg.eca.utils

import org.iisg.eca.domain.Page

/**
 * Represents a menu item
 */
class MenuItem {
    Page page
    List<MenuItem> children
    
    MenuItem(Page page, List<MenuItem> children) {
        this.page = page
        this.children = children
    }
    
    MenuItem(Page page) {
        this.page = page
        this.children = new ArrayList<MenuItem>()
    }
    
    Page getPage() {
        page
    }
    
    List<MenuItem> getChildren() {
        children
    }
    
    void addChild(MenuItem child) {
        children.add(child)
    }
}
