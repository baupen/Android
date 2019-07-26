package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.ApiResponse
import io.mangel.issuemanager.api.Client
import io.mangel.issuemanager.api.DomainOverride
import io.mangel.issuemanager.api.DomainOverrideRoot
import io.mangel.issuemanager.api.Error


class DomainOverridesTask(private val client: Client) : AbstractApiAsyncTask<Any, DomainOverrideRoot>(client) {
    override fun callApi(request: Any, client: Client): ApiResponse<DomainOverrideRoot>? {
        return client.getDomainOverrides()
    }

    override fun onExecutionSuccessful(response: DomainOverrideRoot): ApiCallSucceeded {
        return DomainOverridesTaskFinished(response.domainOverrides)
    }

    override fun onExecutionFailed(error: Error?): ApiCallFailed {
        return DomainOverridesTaskFailed(error)
    }
}

class DomainOverridesTaskFinished(val domainOverrides: List<DomainOverride>) : ApiCallSucceeded()
class DomainOverridesTaskFailed(error: Error?) : ApiCallFailed(error)