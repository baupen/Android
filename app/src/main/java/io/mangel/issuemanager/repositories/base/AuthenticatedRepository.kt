package io.mangel.issuemanager.repositories.base

import io.mangel.issuemanager.events.AuthenticationSuccessfulEvent
import io.mangel.issuemanager.store.AuthenticationToken
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class AuthenticatedRepository {
    init {
        EventBus.getDefault().register(this)
    }

    private var _authenticationToken: AuthenticationToken? = null

    protected fun getAuthenticationToken() = _authenticationToken

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAuthenticationTokenRefreshed(event: AuthenticationSuccessfulEvent) {
        _authenticationToken = event.authenticationToken
    }
}