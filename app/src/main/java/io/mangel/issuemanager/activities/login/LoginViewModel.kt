package io.mangel.issuemanager.activities.login

import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import io.mangel.issuemanager.R
import io.mangel.issuemanager.activities.AbstractLoadingViewModel
import io.mangel.issuemanager.extensions.afterTextChanged
import kotlinx.android.synthetic.main.activity_login.view.*

data class LoginViewModel<T>(private val context: T, private val view: View) : AbstractLoadingViewModel()
        where T : Context, T : LoginViewModel.Login {
    private val viewHolder = ViewHolder(view)

    init {
        viewHolder.usernameExitText.afterTextChanged {
            checkCanSubmit()
        }

        viewHolder.passwordEditText.apply {
            afterTextChanged {
                checkCanSubmit()
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        login()
                }
                false
            }
        }

        viewHolder.createTrialAccountButton.setOnClickListener {
            viewHolder.loadingProgressBar.visibility = View.VISIBLE
            viewHolder.loginWrapper.visibility = View.GONE
            context.createTrialAccount()
        }

        viewHolder.loginButton.setOnClickListener {
            viewHolder.loadingProgressBar.visibility = View.VISIBLE
            viewHolder.loginWrapper.visibility = View.GONE
            login()
        }
    }

    override fun getLoadingIndicator(): ProgressBar {
        return viewHolder.loadingProgressBar
    }

    override fun hideProgressIndicator() {
        super.hideProgressIndicator()
        if (showOnlyWelcomeView) {
            viewHolder.loginWrapper.visibility = View.VISIBLE
        }
    }

    override fun showProgressIndicator() {
        if (showOnlyWelcomeView) {
            super.showProgressIndicator()
        }
        viewHolder.loginWrapper.visibility = View.GONE
    }

    private fun login() {
        context.login(viewHolder.usernameExitText.text.toString(), viewHolder.passwordEditText.text.toString())
    }

    private var passwordTouched = false
    private var usernameTouched = false

    private fun checkCanSubmit() {
        val usernameBlank = viewHolder.usernameExitText.text.isBlank()
        val passwordBlank = viewHolder.passwordEditText.text.isBlank()
        usernameTouched = !usernameBlank || usernameTouched
        passwordTouched = !passwordBlank || passwordTouched

        if (usernameBlank && usernameTouched) {
            viewHolder.usernameExitText.error = context.getString(R.string.invalid_email)
        } else if (passwordBlank && passwordTouched) {
            viewHolder.passwordEditText.error = context.getString(R.string.password_too_short)
        }

        viewHolder.loginButton.isEnabled = !usernameBlank && !passwordBlank;
    }

    interface Login {
        fun login(email: String, password: String)

        fun createTrialAccount()
    }

    fun setUsernamePassword(username: String, password: String) {
        viewHolder.usernameExitText.setText(username, TextView.BufferType.EDITABLE)
        viewHolder.passwordEditText.setText(password, TextView.BufferType.EDITABLE)

        checkCanSubmit()
    }

    private var showOnlyWelcomeView = true

    fun showLoginSuccessful(givenName: String) {
        viewHolder.loginWrapper.visibility = View.GONE
        viewHolder.welcomeWrapper.visibility = View.VISIBLE
        viewHolder.welcomeTextView.text = context.getString(R.string.welcome, givenName);

        showOnlyWelcomeView = false
    }

    class ViewHolder(view: View) {
        val usernameExitText: EditText = view.username
        val passwordEditText: EditText = view.password
        val loginButton: Button = view.login
        val createTrialAccountButton: Button = view.create_trial_account
        val loadingProgressBar: ProgressBar = view.loading
        val loginWrapper: View = view.login_wrapper
        val welcomeWrapper: View = view.welcome_wrapper
        val welcomeTextView: TextView = view.welcome
    }
}