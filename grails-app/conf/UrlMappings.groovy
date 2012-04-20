class UrlMappings {
 	static mappings = {
        "/$event/$date/$controller/$action/$id?" { }

        name eventDate: "/$event/$date/$controller/$action/$id?" { }

        name eventIdx: "/$event/$date" {
            controller = 'event'
            action = 'index'
		}

        name login: "/login/$action" {
            controller = 'login'
        }

        name logout: "/logout/$action" {
            controller = 'logout'
        }

        name event: "/event/$action/$id?" {
            controller = 'event'
        }

		name index: "/" {
            controller = 'event'
            action = 'list'
        }

		"500" (view:'/error')
	}
}
