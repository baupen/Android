package io.mangel.issuemanager.store

import io.mangel.issuemanager.api.User

class StoreConverter {
    fun convert(user: User): io.mangel.issuemanager.store.User {
        return User(user.meta.id, user.meta.lastChangeTime, user.givenName, user.familyName)
    }

    fun getAuthenticationToken(host: String, user: User): AuthenticationToken {
        return AuthenticationToken(host, user.authenticationToken, user.meta.id)
    }
}