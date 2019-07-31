package io.mangel.issuemanager.services

import android.content.Context
import io.mangel.issuemanager.R
import io.mangel.issuemanager.api.Error
import org.jetbrains.anko.longToast
import org.jetbrains.anko.runOnUiThread

class NotificationService(private val context: Context) {
    fun showNotification(notification: Notification) {
        when (notification) {
            Notification.NO_INTERNET_ACCESS -> showToast(R.string.no_internet_access)
            Notification.REQUEST_FAILED -> showToast(R.string.generic_api_error)
        }
    }

    fun showApiError(apiError: Error?) {
        if (apiError == null) {
            return
        }

        when (apiError) {
            Error.OutdatedClient -> showToast(R.string.outdated_application)
            else -> showToast(R.string.generic_api_error)
        }
    }

    private fun showToast(message: Int) {
        context.runOnUiThread {
            longToast(message)
        }
    }
}

enum class Notification {
    NO_INTERNET_ACCESS,
    REQUEST_FAILED
}