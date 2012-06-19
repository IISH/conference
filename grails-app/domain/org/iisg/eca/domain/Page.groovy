package org.iisg.eca.domain

import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib

/**
 * Domain class of table holding all pages of this application
 */
class Page extends DefaultDomain {
    static def pageInformation
    private static final ValidationTagLib MESSAGES = new ValidationTagLib()

    String titleCode
    String titleArg
    String titleDefault
    String controller
    String action
    int sortOrder = 999
    boolean showInMenu = false
    String description
    Page parent

    static hasMany = [  adminPages:     AdminPage,
                        dynamicPages:   DynamicPage,
                        subPages:       Page,
                        groups:         Group]
    static belongsTo = [Page, Group]

    static mapping = {
        table 'pages'
        cache true
        version false
        sort sortOrder: 'asc'

        id              column: 'page_id'
        titleCode       column: 'title_code'
        titleArg        column: 'title_arg'
        titleDefault    column: 'title_default'
        controller      column: 'controller'
        action          column: 'action'
        sortOrder       column: 'sort_order'
        showInMenu      column: 'show_in_menu'
        description     column: 'description',      type: 'text'
        parent          column: 'parent_page_id'

        groups  joinTable: 'groups_pages'
    }

    static constraints = {
        titleCode       nullable: true, maxSize: 50
        titleArg        nullable: true, maxSize: 50
        titleDefault    blank: false,   maxSize: 50
        action          blank: false,   maxSize: 20
        controller      blank: false,   maxSize: 20
        description     nullable: true
        parent          nullable: true
        subPages        nullable: true
    }

    static namedQueries = {
        menuPages() {
            cache(true)

            and {
                eq('showInMenu', true)
                isNull('parent')
            }

            order('sortOrder')
            order('controller')
            order('titleDefault')
        }
    }

    Set<User> getUsers() {
        UserPage.findAllByPage(this).collect { it.user } as Set
    }

    @Override
    def String toString() {
        MESSAGES.message(code: titleCode, args: [MESSAGES.message(code: titleArg)], default: titleDefault).toString()
    }
}
