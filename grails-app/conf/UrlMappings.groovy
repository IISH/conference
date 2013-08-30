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

        // User is separate from an event
        name user: "/user/$action" {
            controller = 'user'
        }
        
        // Event is a controller for ALL events, so it is separate from AN event
        name event: "/event/$action/$id?" {
            controller = 'event'
        }

        // Creating an event date is separate as well, it does not exist yet
        name createEventDate: "/eventDate/create/$id?" {
            controller = 'eventDate'
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
