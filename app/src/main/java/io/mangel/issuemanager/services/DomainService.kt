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

class DomainService {

    fun getDomainOverrider(domainOverrides: List<DomainOverride>, email: String): DomainOverrider {
        val domainOverride = domainOverrides.firstOrNull { it.userInputDomain == email.split("@").last() }

        return DomainOverrider(email, domainOverride)
    }

    class DomainOverrider(private val email: String, private val domainOverride: DomainOverride?) {
        fun getLoginEmail(): String {
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