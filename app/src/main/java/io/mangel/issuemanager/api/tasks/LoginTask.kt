package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.*
import io.mangel.issuemanager.events.TaskFinishedEvent
import java.util.*


class LoginTask(client: Client) : AbstractRestApiCallTask<LoginRequest, LoginResponse>(client) {
    override fun callRestApi(request: LoginRequest, client: Client): ApiResponse<LoginResponse>? {
        return client.login(request)
    }

    override fun onExecutionSuccessful(asyncTaskId: UUID, response: LoginResponse): TaskFinishedEvent {
        return LoginTaskFinished(asyncTaskId, response.user)
    }

    override fun onExecutionFailed(asyncTaskId: UUID, error: Error?): RestApiCallFailed {
        return LoginTaskFailed(asyncTaskId, error)
    }
}

class LoginTaskFinished(taskId: UUID, val user: User) : TaskFinishedEvent(taskId)

class LoginTaskFailed(taskId: UUID, error: Error?) : RestApiCallFailed(taskId, error)