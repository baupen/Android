package io.mangel.issuemanager.data.api.tasks

import io.mangel.issuemanager.data.api.*
import io.mangel.issuemanager.data.events.TaskFinishedEvent
import java.util.*


class LoginTask(client: Client) : AbstractRestApiCallTask<LoginRequest, User>(client) {
    override fun callRestApi(request: LoginRequest, client: Client): Response<User>? {
        return client.login(request)
    }

    override fun onExecutionSuccessful(asyncTaskId: UUID, response: User): TaskFinishedEvent {
        return DomainOverridesTaskFinished(asyncTaskId, response)
    }

    override fun onExecutionFailed(asyncTaskId: UUID, error: Error?): RestApiCallFailed {
        return LoginTaskFailed(asyncTaskId, error)
    }
}

class LoginTaskFinished(taskId: UUID, val user: User) : TaskFinishedEvent(taskId)

class LoginTaskFailed(taskId: UUID, error: Error?) : RestApiCallFailed(taskId, error)