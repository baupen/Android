package io.mangel.issuemanager.activities

import android.view.View
import android.widget.ProgressBar
import io.mangel.issuemanager.services.LoadingService

abstract class AbstractLoadingViewModel : LoadingService.LoadingIndicator {
    protected abstract fun getLoadingIndicator() : ProgressBar

    override fun showIndeterminateProgress() {
        getLoadingIndicator().visibility = View.VISIBLE
        getLoadingIndicator().isIndeterminate = true
    }

    override fun showDeterminateProgress(progress: Int, max: Int) {
        getLoadingIndicator().isIndeterminate = false
        getLoadingIndicator().visibility = View.VISIBLE

        getLoadingIndicator().progress = progress
        getLoadingIndicator().max = max
    }

    override fun hide() {
        getLoadingIndicator().visibility = View.GONE
    }
}