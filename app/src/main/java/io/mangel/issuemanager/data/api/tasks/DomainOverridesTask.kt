package io.mangel.issuemanager.data.api.tasks

import android.os.AsyncTask
import io.mangel.issuemanager.data.api.*
import io.mangel.issuemanager.data.events.TaskFinishedEvent
import io.mangel.issuemanager.data.events.TaskStartedEvent
import org.greenrobot.eventbus.EventBus
import java.util.*


class DomainOverridesTask(client: Client) : AbstractApiCallTask<Any, List<DomainOverride>>(client) {
    override fun callApi(request: Any, client: Client): List<DomainOverride> {
        return client.getDomainOverrides()
    }

    override fun onExecutionFinished(asyncTaskId: UUID, result: List<DomainOverride>): TaskFinishedEvent {
        return DomainOverridesTaskFinished(asyncTaskId, result)
    }
}

class DomainOverridesTaskFinished(taskId: UUID, val domainOverrides: List<DomainOverride>) : TaskFinishedEvent(taskId)