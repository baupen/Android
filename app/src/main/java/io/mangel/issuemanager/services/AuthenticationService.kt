package io.mangel.issuemanager.services

import io.mangel.issuemanager.services.data.SqliteService
import io.mangel.issuemanager.store.AuthenticationToken

class AuthenticationService(private val sqliteService: SqliteService) {
    fun clearUserData() {
        // files will be cleaned up automatically after the next refresh tasks finished
        sqliteService.clearDatabase()
    }

    private var _authenticationToken: AuthenticationToken? = null
    fun setAuthenticationToken(authenticationToken: AuthenticationToken) {
        _authenticationToken = authenticationToken;
    }

    fun getAuthenticationToken(): AuthenticationToken {
        val authenticationToken = _authenticationToken ?: throw IllegalAccessException("Not authenticated");

        return authenticationToken
    }
}