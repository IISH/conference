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

        // Robots.txt
        "/robots.txt" (view: "/robots")

        /*
         * API CALLS
         */

        name api: "/$event/$date/api/$domain" (controller: "api", parseRequest: true) {
            action = [GET: "get", POST: "postPut", PUT: "postPut", DELETE: "delete"]
        }

        name apiLogin: "/$event/$date/api/login" (controller: "api", parseRequest: true) {
            action = 'login'
        }

        name apiChangePassword: "/$event/$date/api/changePassword" (controller: "api", parseRequest: true) {
            action = 'changePassword'
        }

        name apiLostPassword: "/$event/$date/api/lostPassword" (controller: "api", parseRequest: true) {
            action = 'lostPassword'
        }

        name apiConfirmLostPassword: "/$event/$date/api/confirmLostPassword" (controller: "api", parseRequest: true) {
            action = 'confirmLostPassword'
        }

        name accessToken: "/$event/$date/api/accessToken" (controller: "api", parseRequest: true) {
            action = 'accessToken'
        }

	    // TODO: Deprecated, please use programme instead...
	    name apiProgram: "/$event/$date/api/program" (controller: "api", parseRequest: true) {
		    action = 'programme'
	    }

        name apiProgramme: "/$event/$date/api/programme" (controller: "api", parseRequest: true) {
            action = 'programme'
        }

        name sendEmail: "/$event/$date/api/sendEmail" (controller: "api", parseRequest: true) {
            action = 'sendEmail'
        }

        name resendEmail: "/$event/$date/api/resendEmail" (controller: "api", parseRequest: true) {
            action = 'resendEmail'
        }

        name userInfo: "/$event/$date/api/userInfo" (controller: "api", parseRequest: true) {
            action = 'userInfo'
        }

        name settings: "/$event/$date/api/settings" (controller: "api", parseRequest: true) {
            action = 'settings'
        }

	    name translations: "/$event/$date/api/translations" (controller: "api", parseRequest: true) {
		    action = 'translations'
	    }

        name participantsInNetwork: "/$event/$date/api/participantsInNetwork" (controller: "api", parseRequest: true) {
            action = 'participantsInNetwork'
        }

	    name sessionPapersInNetworkXls: "/$event/$date/api/sessionPapersInNetworkXls" (controller: "api", parseRequest: true) {
		    action = 'sessionPapersInNetworkXls'
	    }

	    name individualPapersInNetworkXls: "/$event/$date/api/individualPapersInNetworkXls" (controller: "api", parseRequest: true) {
		    action = 'individualPapersInNetworkXls'
	    }

	    name participantsInSession: "/$event/$date/api/participantsInSession" (controller: "api", parseRequest: true) {
		    action = 'participantsInSession'
	    }

	    name participantsInProposedNetwork: "/$event/$date/api/participantsInProposedNetwork" (controller: "api", parseRequest: true) {
		    action = 'participantsInProposedNetwork'
	    }

	    name sessionsSearch: "/$event/$date/api/sessionsSearch" (controller: "api", parseRequest: true) {
		    action = 'sessionsSearch'
	    }

        name removePaper: "/$event/$date/api/removePaper" (controller: "api", parseRequest: true) {
            action = 'removePaper'
        }

	    name eventDateInfo: "/$event/$date/api/eventDateInfo" (controller: "api", parseRequest: true) {
		    action = 'eventDateInfo'
	    }

	    name eventDats: "/$event/$date/api/eventDates" (controller: "api", parseRequest: true) {
		    action = 'eventDates'
	    }

	    name mailNewPassword: "/$event/$date/api/mailNewPassword" (controller: "api", parseRequest: true) {
		    action = 'mailNewPassword'
	    }

	    name refreshOrder: "/$event/$date/api/refreshOrder" (controller: "api", parseRequest: true) {
		    action = 'refreshOrder'
	    }

        /*
         * END API CALLS
         */

        // Errors:
        "403" (view: '/login/denied')
        "404" (view: '/404')
        "500" (view: '/error')
    }
}
