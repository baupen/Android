package io.mangel.issuemanager.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // do not need this overload
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // do not need this overload
        }
    })
}
