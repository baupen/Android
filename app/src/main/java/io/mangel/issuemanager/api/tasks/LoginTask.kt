package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.*


class LoginTask(val client: Client) : AbstractApiAsyncTask<LoginRequest, LoginResponse>(client) {
    override fun callApi(request: LoginRequest, client: Client): ApiResponse<LoginResponse>? {
        return client.login(request)
    }

    override fun onExecutionSuccessful(response: LoginResponse): ApiCallSucceeded {
        return LoginTaskFinished(client.host, response.user)
    }

    override fun onExecutionFailed(error: Error?): ApiCallFailed {
        return LoginTaskFailed(error)
    }
}

class LoginTaskFinished(val host: String, val user: User) : ApiCallSucceeded()

class LoginTaskFailed(error: Error?) : ApiCallFailed(error)