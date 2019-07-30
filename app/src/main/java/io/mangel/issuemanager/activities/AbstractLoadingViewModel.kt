package io.mangel.issuemanager.activities

import android.view.View
import android.widget.ProgressBar
import io.mangel.issuemanager.services.LoadingService

abstract class AbstractLoadingViewModel : LoadingService.LoadingIndicator {
    protected abstract fun getLoadingIndicator() : ProgressBar

    override fun showIndeterminateProgress() {
        show()
        getLoadingIndicator().isIndeterminate = true
    }

    override fun showDeterminateProgress(progress: Int, max: Int) {
        getLoadingIndicator().isIndeterminate = false
        show()

        getLoadingIndicator().progress = progress
        getLoadingIndicator().max = max
    }

    override fun hide() {
        getLoadingIndicator().visibility = View.GONE
    }

    open fun show() {
        getLoadingIndicator().visibility = View.VISIBLE
    }
}