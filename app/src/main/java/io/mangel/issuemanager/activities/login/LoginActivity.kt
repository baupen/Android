package io.mangel.issuemanager.activities.login

import android.os.Bundle
import io.mangel.issuemanager.R
import io.mangel.issuemanager.activities.AbstractActivity
import io.mangel.issuemanager.activities.overview.OverviewActivity
import io.mangel.issuemanager.api.Error
import io.mangel.issuemanager.api.tasks.CreateTrialAccountTaskFinished
import io.mangel.issuemanager.api.tasks.DomainOverridesTaskFailed
import io.mangel.issuemanager.api.tasks.LoginTaskFailed
import io.mangel.issuemanager.api.tasks.LoginTaskFinished
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*

class LoginActivity : AbstractActivity(), LoginViewModel.Login {
    override fun createTrialAccount() {
        getApplicationFactory().userRepository.createTrialAccount()
    }

    private lateinit var loginViewModel: LoginViewModel<LoginActivity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val loginViewModel = LoginViewModel(this, contentView!!)
        setLoadingViewModel(loginViewModel)

        this.loginViewModel = loginViewModel

        getApplicationFactory().domainRepository.loadDomainOverrides()
        if (getApplicationFactory().userRepository.tryAutomaticLogin()) {
            navigateToOverview()
        }
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDomainOverridesTaskFailed(domainOverridesTaskFailed: DomainOverridesTaskFailed) {
        longToast(R.string.no_internet_access)
    }

    private fun navigateToOverview() {
        startActivity<OverviewActivity>()
    }

    override fun login(email: String, password: String) {
        val repository = getApplicationFactory().userRepository
        repository.login(email, password)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginFailed(loginTaskFailed: LoginTaskFailed) {
        when (loginTaskFailed.error) {
            Error.UnknownUsername -> longToast(R.string.unknown_email)
            Error.WrongPassword -> longToast(R.string.password_wrong)
            else -> getApplicationFactory().notificationService.showApiError(loginTaskFailed.error)
        }
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCreateTrialAccountTaskFinished(event: CreateTrialAccountTaskFinished) {
        alert(
            getString(R.string.trial_account_help, event.trialUser.username, event.trialUser.password),
            getString(R.string.trial_account_created)
        ) {
            yesButton {
                loginViewModel.setUsernamePassword(event.trialUser.username, event.trialUser.password)
                toast("Email & Passwort des Probeaccounts wurden eingefüllt")
            }
        }.show()
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginTaskFinished(loginTaskFinished: LoginTaskFinished) {
        navigateToOverview()
    }
}