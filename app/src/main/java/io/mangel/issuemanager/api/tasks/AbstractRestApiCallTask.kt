package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.*
import io.mangel.issuemanager.events.TaskFinishedEvent
import java.util.*


abstract class AbstractRestApiCallTask<T : Request, T2: Response>(client: Client) :
    AbstractApiCallTask<T, ApiResponse<T2>?>(client) {

    protected abstract fun callRestApi(request: T, client: Client): ApiResponse<T2>?

    protected abstract fun onExecutionFailed(asyncTaskId: UUID, error: Error?): RestApiCallFailed

    protected abstract fun onExecutionSuccessful(asyncTaskId: UUID, response: T2): TaskFinishedEvent

    override fun callApi(client: Client, vararg requests: T): ApiResponse<T2>? {
        return callRestApi(requests.first(), client)
    }

    override fun onExecutionFinished(asyncTaskId: UUID, result: ApiResponse<T2>?): TaskFinishedEvent {
        if (result == null || result.isSuccessful || result.data == null) {
            return onExecutionFailed(asyncTaskId, result?.error)
        } else {
            return onExecutionSuccessful(asyncTaskId, result.data)
        }
    }
}

abstract class RestApiCallFailed(taskId: UUID, val error: Error?) : TaskFinishedEvent(taskId)