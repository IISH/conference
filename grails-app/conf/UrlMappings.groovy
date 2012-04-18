class UrlMappings {
 	static mappings = {
        "/$event/$date" {
			constraints {
			    controller = 'event'
                action = 'index'
			}
		}

         "/$event/$date/$controller/$action/$id?" {
			constraints {
				// apply constraints here
			}
		}

        "/login/$action" {
            constraints {
                controller = 'login'
            }
        }

        "/logout/$action" {
            constraints {
                controller = 'logout'
            }
        }

        "/event/$action/$id?" {
            constraints {
                controller = 'event'
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
