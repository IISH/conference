package org.iisg.eca.security

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler

import javax.servlet.ServletException

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.iisg.eca.domain.User

/**
 * Event listener making sure that the right language cookie is set if the user logs in
 */
class SecurityEventListener extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication)
            throws ServletException, IOException {
        String lang
        //User.withNewSession {
            // Find the language of the logged in user in the database
            lang = User.get(authentication.principal.id).language
        //}

        // If found, update/create the language cookie
        if (lang) {
            Cookie cookie = new Cookie('lang', lang)
            cookie.path = '/'
            response.addCookie(cookie)
        }

        // Continue...
        super.onAuthenticationSuccess(request, response, authentication)
    }
}
