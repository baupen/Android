package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.*
import io.mangel.issuemanager.events.TaskFinishedEvent
import java.util.*


class ReadTask(client: Client) : AbstractRestApiCallTask<ReadRequest, ReadResponse>(client) {
    override fun callRestApi(request: ReadRequest, client: Client): ApiResponse<ReadResponse>? {
        return client.read(request)
    }

    override fun onExecutionSuccessful(asyncTaskId: UUID, response: ReadResponse): TaskFinishedEvent {
        return ReadTaskFinished(asyncTaskId)
    }

    override fun onExecutionFailed(asyncTaskId: UUID, error: Error?): RestApiCallFailed {
        return ReadTaskFailed(asyncTaskId, error)
    }
}

class ReadTaskFinished(taskId: UUID) : TaskFinishedEvent(taskId)

class ReadTaskFailed(taskId: UUID, error: Error?) : RestApiCallFailed(taskId, error)