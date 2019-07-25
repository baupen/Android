package io.mangel.issuemanager.data.api.tasks

import io.mangel.issuemanager.data.api.*
import io.mangel.issuemanager.data.events.TaskFinishedEvent
import java.util.*


class CreateTrialAccountTask(client: Client) : AbstractApiCallTask<TrialRequest, TrialUser>(client) {
    override fun callApi(request: TrialRequest, client: Client): Response<TrialUser>? {
        return client.createTrialAccount(request)
    }

    override fun onExecutionSuccessful(asyncTaskId: UUID, response: TrialUser): TaskFinishedEvent {
        return CreateTrialAccountTaskFinished(asyncTaskId, response)
    }

    override fun onExecutionFailed(asyncTaskId: UUID, error: Error?): ApiCallFailed {
        return CreateTrialAccountTaskFailed(asyncTaskId, error)
    }
}

class CreateTrialAccountTaskFinished(taskId: UUID, val trialUser: TrialUser) : TaskFinishedEvent(taskId)

class CreateTrialAccountTaskFailed(taskId: UUID, error: Error?) : ApiCallFailed(taskId, error)