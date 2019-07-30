package io.mangel.issuemanager.activities

import android.view.View
import android.widget.ProgressBar
import io.mangel.issuemanager.services.LoadingService

abstract class AbstractLoadingViewModel : LoadingService.LoadingIndicator {
    protected abstract fun getLoadingIndicator() : ProgressBar

    override fun showIndeterminateProgress() {
        showProgressIndicator()
        getLoadingIndicator().isIndeterminate = true
    }

    override fun showDeterminateProgress(progress: Int, max: Int) {
        getLoadingIndicator().isIndeterminate = false
        showProgressIndicator()

        getLoadingIndicator().progress = progress
        getLoadingIndicator().max = max
    }

    override fun hideProgressIndicator() {
        getLoadingIndicator().visibility = View.GONE
    }

    open fun showProgressIndicator() {
        getLoadingIndicator().visibility = View.VISIBLE
    }
}