class UrlMappings {
    static mappings = {
        // The default mapping
        "/$event/$date/$controller/$action/$id?" ()
        
        // Again the default mapping, but now with a name
        name eventDate: "/$event/$date/$controller/$action/$id?" ()

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
        "500" (view:'/error')
    }
}
