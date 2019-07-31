package io.mangel.issuemanager.services

import io.mangel.issuemanager.api.DomainOverride

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