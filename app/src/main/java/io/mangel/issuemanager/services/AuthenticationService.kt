package io.mangel.issuemanager.services

import io.mangel.issuemanager.events.SavedConstructionSitesEvent
import io.mangel.issuemanager.events.SavedCraftsmenEvent
import io.mangel.issuemanager.events.SavedIssuesEvent
import io.mangel.issuemanager.events.SavedMapsEvent
import io.mangel.issuemanager.services.data.SqliteService
import io.mangel.issuemanager.services.data.store.AuthenticationToken
import org.greenrobot.eventbus.EventBus

class AuthenticationService(private val sqliteService: SqliteService) {
    fun clearUserData() {
        // files will be cleaned up automatically after the next sync tasks finished
        sqliteService.clearDatabase()

        EventBus.getDefault().post(SavedConstructionSitesEvent())
        EventBus.getDefault().post(SavedMapsEvent())
        EventBus.getDefault().post(SavedIssuesEvent())
        EventBus.getDefault().post(SavedCraftsmenEvent())
    }

    private var _authenticationToken: AuthenticationToken? = null
    fun setAuthenticationToken(authenticationToken: AuthenticationToken) {
        _authenticationToken = authenticationToken
    }

    fun clearAuthenticationToken() {
        _authenticationToken = null
    }

    fun getAuthenticationToken(): AuthenticationToken {
        return _authenticationToken ?: throw IllegalAccessException("Not authenticated")
    }
}