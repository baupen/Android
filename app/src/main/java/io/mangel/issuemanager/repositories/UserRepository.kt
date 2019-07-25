package io.mangel.issuemanager.repositories

import android.util.Base64
import io.mangel.issuemanager.api.Client
import io.mangel.issuemanager.api.LoginRequest
import io.mangel.issuemanager.api.CreateTrialAccountRequest
import io.mangel.issuemanager.api.tasks.CreateTrialAccountTask
import io.mangel.issuemanager.api.tasks.LoginTask
import io.mangel.issuemanager.api.tasks.LoginTaskFinished
import io.mangel.issuemanager.models.User
import io.mangel.issuemanager.services.DomainService
import io.mangel.issuemanager.services.RestHttpService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigInteger
import java.security.MessageDigest


class UserRepository(private val httpService: RestHttpService, private val domainService: DomainService) {
    companion object {
        const val TRIAL_ACCOUNT_HOST = DomainService.DOMAIN_OVERRIDES_HOST
    }

    init {
        EventBus.getDefault().register(this)
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

        val client = Client(
            httpService,
            TRIAL_ACCOUNT_HOST
        )
        val trialAccountTask = CreateTrialAccountTask(client)
        trialAccountTask.execute(loginRequest)
    }

    private lateinit var user: User

    fun getLoggedInUser(): User {
        return user
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginTaskFinished(event: LoginTaskFinished) {
        user = User(event.user.givenName, event.user.familyName)
    }

    private fun getSHA256Hash(text: String): String {
        val md = MessageDigest.getInstance("SHA-256")

        md.update(text.toByteArray())
        val digest = md.digest()

        return String.format("%064x", BigInteger(1, digest))
    }
}