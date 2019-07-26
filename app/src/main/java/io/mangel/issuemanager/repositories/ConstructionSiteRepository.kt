package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.events.AuthenticationSuccessfulEvent
import io.mangel.issuemanager.models.ConstructionSite
import io.mangel.issuemanager.services.DomainService
import io.mangel.issuemanager.services.RestHttpService
import io.mangel.issuemanager.store.AuthenticationToken
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ConstructionSiteRepository(private val httpService: RestHttpService, private val domainService: DomainService) {

    init {
        EventBus.getDefault().register(this)
    }

    private val _constructionSites = ArrayList<ConstructionSite>()
    val constructionSites: List<ConstructionSite> = _constructionSites

    private var authenticationToken: AuthenticationToken? = null

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAuthenticationTokenRefreshed(event: AuthenticationSuccessfulEvent) {
        authenticationToken = event.authenticationToken
    }
}