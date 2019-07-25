package io.mangel.issuemanager.data.api.tasks

import io.mangel.issuemanager.data.api.*
import io.mangel.issuemanager.data.events.TaskFinishedEvent
import java.util.*


abstract class AbstractRestApiCallTask<T : Request, T2>(client: Client) :
    AbstractApiCallTask<T, Response<T2>?>(client) {

    companion object {
        const val STATUS_SUCCESS = "success"
    }

    protected abstract fun callRestApi(request: T, client: Client): Response<T2>?

    protected abstract fun onExecutionFailed(asyncTaskId: UUID, error: Error?): RestApiCallFailed

    protected abstract fun onExecutionSuccessful(asyncTaskId: UUID, response: T2): TaskFinishedEvent

    override fun callApi(request: T, client: Client): Response<T2>? {
        return callRestApi(request, client)
    }

    override fun onExecutionFinished(asyncTaskId: UUID, result: Response<T2>?): TaskFinishedEvent {
        if (result == null || result.status != STATUS_SUCCESS || result.data == null) {
            return onExecutionFailed(asyncTaskId, Error.tryParseFrom(result?.error))
        } else {
            return onExecutionSuccessful(asyncTaskId, result.data)
        }
    }
}

abstract class RestApiCallFailed(taskId: UUID, val error: Error?) : TaskFinishedEvent(taskId)