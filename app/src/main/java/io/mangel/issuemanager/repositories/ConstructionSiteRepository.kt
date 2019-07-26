package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.api.Client
import io.mangel.issuemanager.api.LoginRequest
import io.mangel.issuemanager.api.CreateTrialAccountRequest
import io.mangel.issuemanager.api.tasks.CreateTrialAccountTask
import io.mangel.issuemanager.api.tasks.LoginTask
import io.mangel.issuemanager.api.tasks.LoginTaskFinished
import io.mangel.issuemanager.events.AuthenticationTokenRefreshed
import io.mangel.issuemanager.models.ConstructionSite
import io.mangel.issuemanager.models.User
import io.mangel.issuemanager.services.DomainService
import io.mangel.issuemanager.services.RestHttpService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigInteger
import java.security.MessageDigest


class ConstructionSiteRepository(private val httpService: RestHttpService, private val domainService: DomainService) {

    init {
        EventBus.getDefault().register(this)
    }

    private val _constructionSites = ArrayList<ConstructionSite>()
    val constructionSites: List<ConstructionSite> = _constructionSites

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAuthenticationTokenRefreshed(event: AuthenticationTokenRefreshed) {
    }
}