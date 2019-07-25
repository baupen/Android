package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.*
import io.mangel.issuemanager.events.TaskFinishedEvent
import java.util.*


class CreateTrialAccountTask(client: Client) : AbstractRestApiCallTask<CreateTrialAccountRequest, TrialUser>(client) {
    override fun callRestApi(request: CreateTrialAccountRequest, client: Client): Response<TrialUser>? {
        return client.createTrialAccount(request)
    }

    override fun onExecutionSuccessful(asyncTaskId: UUID, response: TrialUser): TaskFinishedEvent {
        return CreateTrialAccountTaskFinished(asyncTaskId, response)
    }

    override fun onExecutionFailed(asyncTaskId: UUID, error: Error?): RestApiCallFailed {
        return CreateTrialAccountTaskFailed(asyncTaskId, error)
    }
}

class CreateTrialAccountTaskFinished(taskId: UUID, val trialUser: TrialUser) : TaskFinishedEvent(taskId)

class CreateTrialAccountTaskFailed(taskId: UUID, error: Error?) : RestApiCallFailed(taskId, error)