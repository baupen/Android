package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.Client
import io.mangel.issuemanager.api.DomainOverride
import io.mangel.issuemanager.events.TaskFinishedEvent
import java.util.*


class DomainOverridesTask(client: Client) : AbstractApiCallTask<Any, List<DomainOverride>>(client) {
    override fun callApi(client: Client, vararg requests: Any): List<DomainOverride> {
        return client.getDomainOverrides()
    }

    override fun onExecutionFinished(asyncTaskId: UUID, result: List<DomainOverride>): TaskFinishedEvent {
        return DomainOverridesTaskFinished(asyncTaskId, result)
    }
}

class DomainOverridesTaskFinished(taskId: UUID, val domainOverrides: List<DomainOverride>) : TaskFinishedEvent(taskId)