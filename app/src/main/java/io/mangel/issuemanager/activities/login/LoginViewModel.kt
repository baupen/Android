package io.mangel.issuemanager.activities.login

import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
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

        viewHolder.loginButton.setOnClickListener {
            viewHolder.loadingProgressBar.visibility = View.VISIBLE
            login()
        }
    }

    private fun login() {
        context.login(viewHolder.usernameExitText.text.toString(), viewHolder.passwordEditText.text.toString())
    }

    private fun checkCanSubmit() {
        val usernameValid = viewHolder.usernameExitText.text.isNotBlank()
        val passwordValid = viewHolder.passwordEditText.text.isNotBlank()
        viewHolder.loginButton.isEnabled = usernameValid && passwordValid;

        if (!usernameValid) {
            viewHolder.usernameExitText.error = context.getString(R.string.invalid_email)
        } else if (!passwordValid) {
            viewHolder.passwordEditText.error = context.getString(R.string.password_too_short)
        }
    }

    interface Login {
        fun login(email: String, password: String)
    }

    override fun getLoadingIndicator(): ProgressBar {
        return viewHolder.loadingProgressBar
    }

    class ViewHolder(view: View) {
        val usernameExitText: EditText = view.username
        val passwordEditText: EditText = view.password
        val loginButton: Button = view.login
        val loadingProgressBar: ProgressBar = view.loading
    }
}