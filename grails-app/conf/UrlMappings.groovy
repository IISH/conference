class UrlMappings {
    static mappings = {
        // The default mapping
        "/$event/$date/$controller/$action/$id?" ()
        
        // Again the default mapping, but now with a name
        name eventDate: "/$event/$date/$controller/$action/$id?" ()

        // If there is no action specified, always go to the action 'index'
        "/$event/$date/$controller" {
            action = 'index'
        }

        // Login is separate from an event
        name login: "/login/$action" {
            controller = 'login'
        }
        
        // Logout is separate from an event
        name logout: "/logout/$action" {
            controller = 'logout'
        }

        // CSS is separate from an event
        name css: "/css/$action" {
            controller = 'css'
        }

        // User is separate from an event
        name user: "/user/$action" {
            controller = 'user'
        }
        
        // Creating a new event is separate from AN event
        name createEvent: "/event/create" {
            controller = 'event'
            action = 'create'
        }

        // The index page of an event
        name eventIdx: "/$event/$date" {
            controller = 'event'
            action = 'index'
        }
        
        // The default index page
        name index: "/" {
            controller = 'event'
            action = 'list'
        }
        
        // Errors:
        "403" (view: '/login/denied')
        "404" (view: '/404')
        "500" (view: '/error')
    }
}
