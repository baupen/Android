package io.mangel.issuemanager.activities.login

import android.os.Bundle
import io.mangel.issuemanager.R
import io.mangel.issuemanager.activities.AbstractActivity
import io.mangel.issuemanager.activities.overview.OverviewActivity
import io.mangel.issuemanager.api.Error
import io.mangel.issuemanager.api.tasks.LoginTaskFailed
import io.mangel.issuemanager.api.tasks.LoginTaskFinished
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.contentView
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity

class LoginActivity : AbstractActivity(), LoginViewModel.Login {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val loginViewModel = LoginViewModel(this, contentView!!)
        setLoadingViewModel(loginViewModel)

        // start the factory to init domain overrides & potentially sign in the user
        applicationFactory
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginFailed(loginTaskFailed: LoginTaskFailed) {
        when (loginTaskFailed.error) {
            Error.UnknownUsername -> longToast(R.string.unknown_email)
            Error.WrongPassword -> longToast(R.string.password_wrong)
            else -> applicationFactory.notificationService.showApiError(loginTaskFailed.error)
        }
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginTaskFinished(loginTaskFinished: LoginTaskFinished) {
        startActivity<OverviewActivity>()
    }

    override fun login(email: String, password: String) {
        val repository = applicationFactory.userRepository
        repository.login(email, password)
    }
}