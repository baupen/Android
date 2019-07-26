package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.*
import io.mangel.issuemanager.events.TaskFinishedEvent
import io.mangel.issuemanager.events.TaskStartedEvent
import org.greenrobot.eventbus.EventBus
import java.util.*


abstract class AbstractRestApiCallTask<T, T2 : Response>(client: Client) :
    AbstractApiCallTask<T, ApiResponse<T2>?>(client) {

    protected abstract fun callRestApi(request: T, client: Client): ApiResponse<T2>?

    protected abstract fun onExecutionFailed(error: Error?): RestApiCallFailed

    protected abstract fun onExecutionSuccessful(response: T2): RestApiCallSucceeded

    override fun callApi(client: Client, vararg requests: T): ApiResponse<T2>? {
        return callRestApi(requests.first(), client)
    }

    override fun onExecutionFinished(result: ApiResponse<T2>?): RestApiCallFinished {
        if (result == null || result.isSuccessful || result.data == null) {
            return onExecutionFailed(result?.error)
        } else {
            return onExecutionSuccessful(result.data)
        }
    }
}

abstract class RestApiCallFinished

abstract class RestApiCallSucceeded : RestApiCallFinished()
abstract class RestApiCallFailed(val error: Error?) : RestApiCallFinished()