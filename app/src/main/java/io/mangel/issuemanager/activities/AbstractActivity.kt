package io.mangel.issuemanager.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.mangel.issuemanager.R
import io.mangel.issuemanager.api.Error
import io.mangel.issuemanager.api.tasks.LoginTaskFailed
import io.mangel.issuemanager.events.TaskFinishedEvent
import io.mangel.issuemanager.events.TaskStartedEvent
import io.mangel.issuemanager.factories.ApplicationFactory
import io.mangel.issuemanager.services.LoadingService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.longToast

abstract class AbstractActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().register(this)
    }

    private var loadingService: LoadingService? = null

    protected fun setLoadingViewModel(loadingViewModel: AbstractLoadingViewModel) {
        this.loadingService = LoadingService(loadingViewModel)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginFailed(taskStartedEvent: TaskStartedEvent) {
        loadingService?.onTaskStartedEventReceived(taskStartedEvent)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginFailed(taskEndedEvent: TaskFinishedEvent) {
        loadingService?.onTaskFinishedEventReceived(taskEndedEvent)
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)
    }

    protected val applicationFactory = ApplicationFactory.getInstance(this)
}