package io.mangel.issuemanager.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.mangel.issuemanager.events.TaskProgressEvent
import io.mangel.issuemanager.events.TaskFinishedEvent
import io.mangel.issuemanager.events.TaskStartedEvent
import io.mangel.issuemanager.factories.ApplicationFactory
import io.mangel.issuemanager.services.LoadingService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class AbstractActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().register(this)
    }

    private var _loadingService: LoadingService? = null

    protected fun setLoadingViewModel(loadingViewModel: AbstractLoadingViewModel) {
        this._loadingService = LoadingService(loadingViewModel)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTaskStarted(taskStartedEvent: TaskStartedEvent) {
        _loadingService?.onStarted(taskStartedEvent)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTaskProgress(taskProgressEvent: TaskProgressEvent) {
        _loadingService?.onProgress(taskProgressEvent)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTaskFinished(taskFinishedEvent: TaskFinishedEvent) {
        _loadingService?.onFinished(taskFinishedEvent)
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)
    }

    protected fun getApplicationFactory() = ApplicationFactory.getInstance(this)
}