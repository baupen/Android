package io.mangel.issuemanager.data.api.tasks

import io.mangel.issuemanager.data.api.*
import io.mangel.issuemanager.data.events.TaskFinishedEvent
import java.util.*


class LoginTask(client: Client) : AbstractApiCallTask<LoginRequest, User>(client) {
    override fun callApi(request: LoginRequest, client: Client): Response<User>? {
        return client.login(request)
    }

    override fun onExecutionSuccessful(asyncTaskId: UUID, response: User): TaskFinishedEvent {
        return LoginTaskFinished(asyncTaskId, response)
    }

    override fun onExecutionFailed(asyncTaskId: UUID, error: Error?): ApiCallFailed {
        return LoginTaskFailed(asyncTaskId, error)
    }
}

class LoginTaskFinished(taskId: UUID, val user: User) : TaskFinishedEvent(taskId)

class LoginTaskFailed(taskId: UUID, error: Error?) : ApiCallFailed(taskId, error)