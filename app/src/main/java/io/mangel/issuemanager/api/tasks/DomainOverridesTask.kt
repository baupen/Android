package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.Client
import io.mangel.issuemanager.api.DomainOverride
import io.mangel.issuemanager.events.TaskFinishedEvent
import java.util.*


class DomainOverridesTask(client: Client) : AbstractApiCallTask<Any, List<DomainOverride>>(client) {
    override fun callApi(client: Client, vararg requests: Any): List<DomainOverride> {
        return client.getDomainOverrides()
    }

    override fun onExecutionFinished(result: List<DomainOverride>): RestApiCallSucceeded {
        return DomainOverridesTaskFinished(result)
    }
}

class DomainOverridesTaskFinished(val domainOverrides: List<DomainOverride>) : RestApiCallSucceeded()