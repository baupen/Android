package io.mangel.issuemanager.data.repositories

import android.util.Base64
import io.mangel.issuemanager.data.api.Client
import io.mangel.issuemanager.data.api.LoginRequest
import io.mangel.issuemanager.data.api.CreateTrialAccountRequest
import io.mangel.issuemanager.data.api.tasks.CreateTrialAccountTask
import io.mangel.issuemanager.data.api.tasks.LoginTask
import io.mangel.issuemanager.services.DomainService
import io.mangel.issuemanager.services.RestHttpService
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class UserRepository(private val httpService: RestHttpService, private val domainService: DomainService) {
    companion object {
        const val TRIAL_ACCOUNT_HOST = DomainService.DOMAIN_OVERRIDES_HOST
    }

    fun login(email: String, password: String) {
        domainService.initialize(email)

        val passwordHash = getSHA256Hash(password)
        val username = domainService.getLoginEmail()
        val loginRequest = LoginRequest(username, passwordHash)

        val client = Client(httpService, domainService.getHost())
        val loginTask = LoginTask(client)
        loginTask.execute(loginRequest)
    }

    fun createTrialAccount(proposedGivenName: String, proposedFamilyName: String) {
        val loginRequest = CreateTrialAccountRequest(proposedGivenName, proposedFamilyName)

        val client = Client(httpService, TRIAL_ACCOUNT_HOST)
        val trialAccountTask = CreateTrialAccountTask(client)
        trialAccountTask.execute(loginRequest)
    }

    private fun getSHA256Hash(text: String): String {
        val md = MessageDigest.getInstance("SHA-256")

        md.update(text.toByteArray())
        val digest = md.digest()

        return Base64.encodeToString(digest, Base64.DEFAULT)
    }
}