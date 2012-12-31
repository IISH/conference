package org.iisg.eca.domain

import org.springframework.context.i18n.LocaleContextHolder
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Domain class of table holding all pages of this application
 */
class Page extends DefaultDomain {
    def springSecurityService
    def messageSource

    static def pageInformation

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
        controller      nullable: true, maxSize: 20
        action          nullable: true, maxSize: 20
        description     nullable: true
        parent          nullable: true
        subPages        nullable: true
    }

    static namedQueries = {
        menuPages {
            cache(true)

            and {
                eq('showInMenu', true)
                isNull('parent')
            }

            order('sortOrder')
            order('controller')
            order('titleDefault')
        }

        subMenuPages { parentPage ->
            cache(true)

            and {
                eq('showInMenu', true)
                eq('parent', parentPage)
            }

            order('sortOrder')
            order('controller')
            order('titleDefault')
        }
    }
    
    static Set<Page> getAllPagesWithAccess() {
        Set<Page> pages = []
        Page.list().each { 
            if (it.hasAccess()) {
                pages.add(it) 
            }
        }
        pages
    }

    Set<User> getUsers() {
        UserPage.findAllByPage(this).collect { it.user } as Set
    }

    /**
     * Determine if the currently logged in user has access to this page
     * @return A boolean indicating access or not
     */
    boolean hasAccess() {
        if (SpringSecurityUtils.ifNotGranted('superAdmin') && !(controller == null && action == null)) {
            User user = User.get(springSecurityService.principal.id)
            UserPage userPage = UserPage.findAllByUser(user).find { (it.page.controller == controller) && (it.page.action == action) }

            // An individual rule for this page has been configured for this user
            if (userPage) {
                return !userPage.denied
            }
            else {
                // Look in all the groups assigned to this user, whether the requested page is among them
                List<Long> count = Group.withCriteria {
                    cache(true)

                    projections {
                        count()
                    }

                    users {
                        eq('id', user.id)
                    }

                    pages {
                        eq('controller', controller)
                        eq('action', action)
                    }
                }

                // If the page was not found, deny access
                return (count.first() > 0)
            }
        }

        return true
    }

    @Override
    def String toString() {
        Object[] args = null
        if (titleArg) {
            args = [messageSource.getMessage(titleArg, null, LocaleContextHolder.locale)]
        }

        messageSource.getMessage(titleCode, args, titleDefault, LocaleContextHolder.locale)
    }
}
