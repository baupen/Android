package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.*


class LoginTask(client: Client) : AbstractRestApiCallTask<LoginRequest, LoginResponse>(client) {
    override fun callRestApi(request: LoginRequest, client: Client): ApiResponse<LoginResponse>? {
        return client.login(request)
    }

    override fun onExecutionSuccessful(response: LoginResponse): RestApiCallSucceeded {
        return LoginTaskFinished(response.user)
    }

    override fun onExecutionFailed(error: Error?): RestApiCallFailed {
        return LoginTaskFailed(error)
    }
}

class LoginTaskFinished(val user: User) : RestApiCallSucceeded()

class LoginTaskFailed(error: Error?) : RestApiCallFailed(error)