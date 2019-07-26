package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.*


class CreateTrialAccountTask(client: Client) :
    AbstractApiAsyncTask<CreateTrialAccountRequest, CreateTrialAccountResponse>(client) {
    override fun callApi(
        request: CreateTrialAccountRequest,
        client: Client
    ): ApiResponse<CreateTrialAccountResponse>? {
        return client.createTrialAccount(request)
    }

    override fun onExecutionSuccessful(response: CreateTrialAccountResponse): ApiCallSucceeded {
        return CreateTrialAccountTaskFinished(response.trialUser)
    }

    override fun onExecutionFailed(error: Error?): ApiCallFailed {
        return CreateTrialAccountTaskFailed(error)
    }
}

class CreateTrialAccountTaskFinished(val trialUser: TrialUser) : ApiCallSucceeded()

class CreateTrialAccountTaskFailed(error: Error?) : ApiCallFailed(error)