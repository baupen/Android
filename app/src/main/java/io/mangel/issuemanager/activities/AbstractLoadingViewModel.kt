package io.mangel.issuemanager.activities

import android.view.View
import android.widget.ProgressBar
import io.mangel.issuemanager.services.LoadingService

abstract class AbstractLoadingViewModel : LoadingService.LoadingIndicator {
    protected abstract fun getLoadingIndicator() : ProgressBar

    override fun hideLoadingIndicator() {
        getLoadingIndicator().visibility = View.GONE
    }

    override fun showLoadingIndicator() {
        getLoadingIndicator().visibility = View.VISIBLE
    }

}