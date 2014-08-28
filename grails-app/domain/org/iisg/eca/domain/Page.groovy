package org.iisg.eca.domain

import org.iisg.eca.utils.MenuItem
import org.springframework.context.i18n.LocaleContextHolder
import grails.plugin.springsecurity.SpringSecurityUtils

/**
 * Domain class of table holding all pages of this application
 */
class Page {
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
    String urlQuery

    static hasMany = [  dynamicPages:   DynamicPage,
                        subPages:       Page,
                        groups:         Group]
    static belongsTo = [Page, Group]

    static mapping = {
        table 'pages'
        version false
        sort controller: 'asc'

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
        urlQuery        column: 'url_query'

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
        urlQuery        nullable: true, maxSize: 100
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
    
    static List<Page> getAllPagesWithAccess() {
        List<Page> pages = []
        Page.listOrderByTitleDefault().each {
            if (it.hasAccess() && (it.controller != null) && (it.action != null)) {
                pages.add(it) 
            }
        }
        pages
    }
    
    static List<MenuItem> getMenu() {
        getSubMenu(Page.menuPages().list())
    }
    
    private static List<MenuItem> getSubMenu(List<Page> pages) {
        List<MenuItem> menu = []
        pages.each { page -> 
            if (page.hasAccess()) {
                MenuItem item = new MenuItem(page, getSubMenu(Page.subMenuPages(page).list()))       
                menu.add(item)
            }
        }
        menu
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
            UserPage userPage = UserPage.findAllByUser(user, [cache: true]).find { (it.page.controller == controller) && (it.page.action == action) }

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
    
    /**
     * Return the query part of the URL for this page
     * @return The query part of a URL
     */
    String getUrlQuery() {
        String cleanUrlQuery = urlQuery
        
        if (cleanUrlQuery != null && (cleanUrlQuery.startsWith("?") || cleanUrlQuery.startsWith("&"))) {
            cleanUrlQuery = cleanUrlQuery[1..-1]
        }
        
        cleanUrlQuery 
    }

    /**
     * The title of the page with action
     * @return The title of the page with action
     */
    String getTitleWithAction() {
        "${toString()} (${action})"
    }

	/**
	 * Will attempt to generate a user-friendly title without strange capitalization's
	 * @param locale The locale to use, will default to currently hold locale
	 * @return The title of this page
	 */
	String getTitle(Locale locale = null) {
		locale = locale ?: LocaleContextHolder.getLocale()

		Object[] args = []
		if (titleArg) {
			args = [messageSource.getMessage(titleArg, null, locale).toLowerCase(locale)]
		}

		return messageSource.getMessage(titleCode, args, titleDefault, locale).capitalize()
	}

    @Override
    String toString() {
	    getTitle()
    }
}
