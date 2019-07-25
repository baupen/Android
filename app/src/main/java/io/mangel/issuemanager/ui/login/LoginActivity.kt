package io.mangel.issuemanager.ui.login

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast

import io.mangel.issuemanager.R
import io.mangel.issuemanager.factories.RepositoryFactory
import kotlinx.android.synthetic.main.activity_login.view.*
import org.jetbrains.anko.contentView

class LoginActivity : AppCompatActivity() {

    private lateinit var viewHolder: ViewHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        viewHolder = ViewHolder(contentView!!)
        RepositoryFactory.getInstance(this).userRepository

        /**
         * TODO: put form validation in view model
         */
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

    private fun checkCanSubmit() {
        val usernameValid = viewHolder.usernameExitText.text.isNotBlank()
        val passwordValid = viewHolder.passwordEditText.text.isNotBlank()
        viewHolder.loginButton.isEnabled = usernameValid && passwordValid;

        if (!usernameValid) {
            viewHolder.usernameExitText.error = getString(R.string.invalid_email)
        } else if (!passwordValid) {
            viewHolder.passwordEditText.error = getString(R.string.password_too_short)
        }
    }

    private fun login() {
        val repository = RepositoryFactory.getInstance(this).userRepository
        repository.login(viewHolder.usernameExitText.text.toString(), viewHolder.passwordEditText.text.toString())
    }

    class ViewHolder(view: View) {
        val usernameExitText: EditText = view.username
        val passwordEditText: EditText = view.password
        val loginButton: Button = view.login
        val loadingProgressBar: ProgressBar = view.loading
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
