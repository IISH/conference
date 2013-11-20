package org.iisg.eca.security

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Setting

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.authentication.dao.ReflectionSaltSource

/**
 * Extension of the <code>ReflectionSaltSource</code> class to allow a combination of a static salt and user salts
 */
class UserSaltSource extends ReflectionSaltSource {
    /**
     * Get the salt used to authenticate the given user
     * @param user The user in question
     * @return The salt to authenticate the given user
     */
    @Override
    Object getSalt(UserDetails user) {
        createSalt(user[userPropertyToUse])
    }

    /**
     * Get the salt used to authenticate the given user
     * @param user The user in question
     * @return The salt to authenticate the given user
     */
    Object getSalt(User user) {
        createSalt(user[userPropertyToUse])
    }

    /**
     * Creates a salt by combining the static salt with the given user salt
     * @param userSalt The user salt in question
     * @return A new salt combining the static salt with the given user salt
     */
    private static Object createSalt(String userSalt) {
        // Get the static salt from the settings table
        String staticSalt = Setting.getSetting(Setting.SALT).value
        staticSalt + userSalt
    }
}
