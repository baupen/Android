package io.mangel.issuemanager.services

import io.mangel.issuemanager.events.TaskFinishedEvent
import io.mangel.issuemanager.events.TaskStartedEvent
import java.util.*
import kotlin.collections.HashSet

class LoadingService(private val loadingIndicator: LoadingIndicator) {

    private val activeTasks = HashSet<UUID>()

    fun onTaskStartedEventReceived(taskStartedEvent: TaskStartedEvent) {
        activeTasks.add(taskStartedEvent.taskId)

        loadingIndicator.showLoadingIndicator()
    }

    fun onTaskFinishedEventReceived(taskFinishedEvent: TaskFinishedEvent) {
        activeTasks.remove(taskFinishedEvent.taskId)

        if (activeTasks.isEmpty()) {
            loadingIndicator.hideLoadingIndicator()
        }
    }

    interface LoadingIndicator {
        fun hideLoadingIndicator()

        fun showLoadingIndicator()
    }
}