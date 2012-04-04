class UrlMappings {
 	static mappings = {
		"/$controller/$action?/$id?" {
			constraints {
				// apply constraints here
			}
		}

		"/" {
            constraints {
                controller = 'event'
                action = 'list'
            }
        }

		"500" (view:'/error')
	}
}
