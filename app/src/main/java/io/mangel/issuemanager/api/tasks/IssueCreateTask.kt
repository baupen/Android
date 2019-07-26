package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.*
import io.mangel.issuemanager.events.TaskFinishedEvent
import java.util.*


class IssuePersistTask(client: Client) : AbstractRestApiCallTask<IssueWithImagePayload, IssueResponse>(client) {
    override fun callRestApi(request: IssueWithImagePayload, client: Client): ApiResponse<IssueResponse>? {
        return when (request.createOrUpdate) {
            CreateOrUpdate.Create -> client.issueCreate(request.issueRequest, request.filePath, request.fileName)
            CreateOrUpdate.Update -> client.issueUpdate(request.issueRequest, request.filePath, request.fileName)
        }
    }

    override fun onExecutionSuccessful(response: IssueResponse): RestApiCallSucceeded {
        return IssuePersistTaskFinished(response.issue)
    }

    override fun onExecutionFailed(error: Error?): RestApiCallFailed {
        return IssuePersistTaskFailed(error)
    }
}

class IssueWithImagePayload(
    val createOrUpdate: CreateOrUpdate,
    val issueRequest: IssueRequest,
    val filePath: String,
    val fileName: String
)

enum class CreateOrUpdate {
    Create,
    Update
}

class IssuePersistTaskFinished(val issue: Issue) : RestApiCallSucceeded()

class IssuePersistTaskFailed(error: Error?) : RestApiCallFailed(error)