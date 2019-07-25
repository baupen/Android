package io.mangel.issuemanager.services

import io.mangel.issuemanager.api.Client
import io.mangel.issuemanager.api.DomainOverride
import io.mangel.issuemanager.api.tasks.DomainOverridesTask
import io.mangel.issuemanager.api.tasks.DomainOverridesTaskFinished
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.IllegalArgumentException
import kotlin.collections.ArrayList

class DomainService(private val httpService: RestHttpService) {

    companion object {
        const val DOMAIN_OVERRIDES_HOST = "https://app.mangel.io"
    }

    private lateinit var domainOverrides: List<DomainOverride>

    init {
        EventBus.getDefault().register(this)

        loadDomainOverrides()
    }

    private fun loadDomainOverrides() {
        val client = Client(httpService, DOMAIN_OVERRIDES_HOST)
        val domainOverridesTask = DomainOverridesTask(client)
        domainOverridesTask.execute()
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDomainOverridesTaskFinished(event: DomainOverridesTaskFinished) {
        domainOverrides = event.domainOverrides
    }

    private var email: String? = null

    fun initialize(email: String) {
        this.email = email;
    }

    fun getLoginEmail(): String {
        return getDomainOverriderOrThrow().transformToLoginEmail()
    }

    fun getHost(): String {
        return getDomainOverriderOrThrow().getHost()
    }


    private fun getDomainOverriderOrThrow(): DomainOverrider {
        val email = email
            ?: throw IllegalArgumentException("must initialize the domain service before calling any of its other functions")

        val domainOverride = domainOverrides.firstOrNull { it.userInputDomain == email }

        return DomainOverrider(email, domainOverride)
    }

    private class DomainOverrider(private val email: String, private val domainOverride: DomainOverride?) {
        fun transformToLoginEmail(): String {
            if (domainOverride === null) {
                return email
            }

            val emailParts = email.split("@")
            return emailParts.first() + "@" + domainOverride.userLoginDomain
        }

        fun getHost(): String {
            if (domainOverride === null) {
                return "https://app.mangel.io"
            }

            return domainOverride.serverURL
        }
    }
}