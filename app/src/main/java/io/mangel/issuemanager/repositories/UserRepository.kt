package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.api.Client
import io.mangel.issuemanager.api.LoginRequest
import io.mangel.issuemanager.api.CreateTrialAccountRequest
import io.mangel.issuemanager.api.tasks.*
import io.mangel.issuemanager.events.AuthenticationSuccessfulEvent
import io.mangel.issuemanager.events.UserLoadedEvent
import io.mangel.issuemanager.events.UserRefreshedEvent
import io.mangel.issuemanager.factories.ClientFactory
import io.mangel.issuemanager.models.ModelConverter
import io.mangel.issuemanager.models.User
import io.mangel.issuemanager.services.*
import io.mangel.issuemanager.services.SettingService
import io.mangel.issuemanager.services.data.SqliteService
import io.mangel.issuemanager.store.AuthenticationToken
import io.mangel.issuemanager.store.StoreConverter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigInteger
import java.security.MessageDigest


class UserRepository(
    private val domainServiceRepository: DomainOverridesRepository,
    private val domainService: DomainService,
    private val storeConverter: StoreConverter,
    private val modelConverter: ModelConverter,
    private val settingService: SettingService,
    private val clientFactory: ClientFactory
) {
    companion object {
        const val TRIAL_ACCOUNT_HOST = DomainOverridesRepository.DOMAIN_OVERRIDES_HOST
    }

    init {
        EventBus.getDefault().register(this)
    }

    fun tryAutomaticLogin(): Boolean {
        val token = settingService.readAuthenticationToken() ?: return false

        val authenticationSuccessfulEvent = AuthenticationSuccessfulEvent(token)
        EventBus.getDefault().post(authenticationSuccessfulEvent)

        return true
    }

    fun login(email: String, password: String) {
        val domainOverrider = domainService.getDomainOverrider(domainServiceRepository.domainOverrides, email)

        val passwordHash = getSHA256Hash(password)
        val username = domainOverrider.getLoginEmail()
        val loginRequest = LoginRequest(username, passwordHash)

        val client = clientFactory.getClient(domainOverrider.getHost())
        val loginTask = LoginTask(client)
        loginTask.execute(loginRequest)
    }

    fun createTrialAccount(proposedGivenName: String, proposedFamilyName: String) {
        val loginRequest = CreateTrialAccountRequest(proposedGivenName, proposedFamilyName)

        val client = clientFactory.getClient(TRIAL_ACCOUNT_HOST)
        val trialAccountTask = CreateTrialAccountTask(client)
        trialAccountTask.execute(loginRequest)
    }

    private var user: User? = null
    private var authenticationToken: AuthenticationToken? = null

    fun getLoggedInUser(): User? {
        val authenticationToken = authenticationToken
        if (user == null && authenticationToken != null) {
            val storeUser = settingService.readUser() ?: return null
            val user = modelConverter.convert(storeUser)
            EventBus.getDefault().post(UserLoadedEvent(user))

            this.user = user
        }

        return user
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserRefreshedEvent(event: UserRefreshedEvent) {
        user = event.user
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginTaskFinished(event: LoginTaskFinished) {
        val storeUser = storeConverter.convert(event.user)
        settingService.saveUser(storeUser)

        user = modelConverter.convert(storeUser)

        val authenticationToken = storeConverter.getAuthenticationToken(event.host, event.user)
        val authenticationTokenRefreshedEvent = AuthenticationSuccessfulEvent(authenticationToken)
        EventBus.getDefault().post(authenticationTokenRefreshedEvent)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAuthenticationSuccessfulEvent(event: AuthenticationSuccessfulEvent) {
        this.authenticationToken = event.authenticationToken
        settingService.saveAuthenticationToken(event.authenticationToken)
    }

    private fun getSHA256Hash(text: String): String {
        val md = MessageDigest.getInstance("SHA-256")

        md.update(text.toByteArray())
        val digest = md.digest()

        return String.format("%064x", BigInteger(1, digest))
    }
}