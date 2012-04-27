package org.iisg.eca.domain

import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib

/**
 * Domain class of table holding all pages of this application
 */
class Page extends DefaultDomain {
    def pageInformation
    private static final ValidationTagLib MESSAGES = new ValidationTagLib()

    String titleCode
    String titleArg
    String titleDefault
    String controller
    String action
    String description
    Page parent

    static hasMany = [  adminPages:     AdminPage,
                        dynamicPages:   DynamicPage,
                        subPages:       Page]
    static belongsTo = Page

    static mapping = {
        table 'pages'
        cache true
        version false

        id              column: 'page_id'
        titleCode       column: 'title_code'
        titleArg        column: 'title_arg'
        titleDefault    column: 'title_default'
        controller      column: 'controller'
        action          column: 'action'
        description     column: 'description',      type: 'text'
        parent          column: 'parent_page_id'
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

    Set<User> getUsers() {
        UserPage.findAllByPage(this).collect { it.user } as Set
    }

    Set<Group> getGroups() {
        GroupPage.findAllByPage(this).collect { it.group } as Set
    }

    @Override
    def String toString() {
        MESSAGES.message(code: titleCode, args: [MESSAGES.message(code: titleArg)], default: titleDefault).toString()
    }
}
