package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.*
import io.mangel.issuemanager.events.TaskFinishedEvent
import java.util.*


class CreateTrialAccountTask(client: Client) :
    AbstractRestApiCallTask<CreateTrialAccountRequest, CreateTrialAccountResponse>(client) {
    override fun callRestApi(
        request: CreateTrialAccountRequest,
        client: Client
    ): ApiResponse<CreateTrialAccountResponse>? {
        return client.createTrialAccount(request)
    }

    override fun onExecutionSuccessful(response: CreateTrialAccountResponse): RestApiCallSucceeded {
        return CreateTrialAccountTaskFinished(response.trialUser)
    }

    override fun onExecutionFailed(error: Error?): RestApiCallFailed {
        return CreateTrialAccountTaskFailed(error)
    }
}

class CreateTrialAccountTaskFinished(val trialUser: TrialUser) : RestApiCallSucceeded()

class CreateTrialAccountTaskFailed(error: Error?) : RestApiCallFailed(error)