package io.mangel.issuemanager.services

import io.mangel.issuemanager.events.TaskProgressEvent
import io.mangel.issuemanager.events.TaskFinishedEvent
import io.mangel.issuemanager.events.TaskStartedEvent
import java.util.*

class LoadingService(private val loadingIndicator: LoadingIndicator) {

    private var taskActive: MutableMap<UUID, Boolean> = HashMap()
    private var progressByTask: MutableMap<UUID, Int> = HashMap()
    private var maxByTask: HashMap<UUID, Int> = HashMap()

    fun onStarted(event: TaskStartedEvent) {
        if (!taskActive.any()) {
            loadingIndicator.showIndeterminateProgress()
        }

        taskActive[event.taskId] = true;
    }

    fun onProgress(event: TaskProgressEvent) {
        progressByTask[event.taskId] = event.progress;
        maxByTask[event.taskId] = event.max;

        showDeterminateProgress()
    }

    fun onFinished(event: TaskFinishedEvent) {
        taskActive[event.taskId] = false;
        progressByTask[event.taskId] = maxByTask[event.taskId]!!;

        if (taskActive.values.none { it }) {
            taskActive = HashMap();
            progressByTask = HashMap();
            maxByTask = HashMap();

            loadingIndicator.hideProgressIndicator()
        } else {
            showDeterminateProgress()
        }
    }

    private fun showDeterminateProgress() {
        loadingIndicator.showDeterminateProgress(progressByTask.values.sum(), maxByTask.values.sum())
    }

    interface LoadingIndicator {
        fun showIndeterminateProgress()

        fun showDeterminateProgress(progress: Int, max: Int)

        fun hideProgressIndicator()
    }
}