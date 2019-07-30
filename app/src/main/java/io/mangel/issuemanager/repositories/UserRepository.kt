package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.api.LoginRequest
import io.mangel.issuemanager.api.CreateTrialAccountRequest
import io.mangel.issuemanager.api.tasks.*
import io.mangel.issuemanager.events.UserSavedEvent
import io.mangel.issuemanager.factories.ClientFactory
import io.mangel.issuemanager.models.ModelConverter
import io.mangel.issuemanager.models.User
import io.mangel.issuemanager.services.*
import io.mangel.issuemanager.services.SettingService
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
    private val clientFactory: ClientFactory,
    private val authenticationService: AuthenticationService
) {
    companion object {
        const val TRIAL_ACCOUNT_HOST = DomainOverridesRepository.DOMAIN_OVERRIDES_HOST
    }

    init {
        EventBus.getDefault().register(this)
    }

    fun tryAutomaticLogin(): Boolean {
        val token = settingService.readAuthenticationToken() ?: return false
        authenticationService.setAuthenticationToken(token)

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

    fun createTrialAccount() {
        val loginRequest = CreateTrialAccountRequest()

        val client = clientFactory.getClient(TRIAL_ACCOUNT_HOST)
        val trialAccountTask = CreateTrialAccountTask(client)
        trialAccountTask.execute(loginRequest)
    }

    private var _user: User? = null

    fun getLoggedInUser(): User {
        var user = _user

        if (user == null) {
            val storeUser = settingService.readUser() ?: throw IllegalAccessException("No user saved")
            user = modelConverter.convert(storeUser)

            this._user = user
        }

        return user
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(event: UserSavedEvent) {
        _user = event.user
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginTaskFinished(event: LoginTaskFinished) {
        val previousUser = settingService.readUser()
        if (previousUser != null && previousUser.id != event.user.meta.id) {
            authenticationService.clearUserData()
        }

        val storeUser = storeConverter.convert(event.user)

        settingService.saveUser(storeUser)

        _user = modelConverter.convert(storeUser)

        val authenticationToken = storeConverter.getAuthenticationToken(event.host, event.user)
        authenticationService.setAuthenticationToken(authenticationToken)
        settingService.saveAuthenticationToken(authenticationToken)
    }

    private fun getSHA256Hash(text: String): String {
        val md = MessageDigest.getInstance("SHA-256")

        md.update(text.toByteArray())
        val digest = md.digest()

        return String.format("%064x", BigInteger(1, digest))
    }
}