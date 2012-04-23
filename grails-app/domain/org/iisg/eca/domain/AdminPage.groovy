package org.iisg.eca.domain

/**
 * Domain class of table holding all admin pages per event
 */
class AdminPage extends EventDomain {
    Page page
    boolean showInMenu = true
    
    static belongsTo = Page
    
    static mapping = {
        table 'admin_pages'
        version false

        id          column: 'admin_page_id'
        page        column: 'page_id'
        showInMenu  column: 'show_in_menu'
    }
}
