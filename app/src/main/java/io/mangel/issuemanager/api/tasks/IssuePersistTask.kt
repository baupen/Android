package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.*


class IssuePersistTask(client: Client) : AbstractApiAsyncTask<IssuePersistPayload, IssueResponse>(client) {
    override fun callApi(request: IssuePersistPayload, client: Client): ApiResponse<IssueResponse>? {
        return when (request.createOrUpdate) {
            CreateOrUpdate.Create -> client.issueCreate(request.issueRequest, request.filePath, request.fileName)
            CreateOrUpdate.Update -> client.issueUpdate(request.issueRequest, request.filePath, request.fileName)
        }
    }

    override fun onExecutionSuccessful(response: IssueResponse): ApiCallSucceeded {
        return IssuePersistTaskFinished(response.issue)
    }

    override fun onExecutionFailed(error: Error?): ApiCallFailed {
        return IssuePersistTaskFailed(error)
    }
}

class IssuePersistPayload(
    val createOrUpdate: CreateOrUpdate,
    val issueRequest: IssueRequest,
    val filePath: String,
    val fileName: String
)

enum class CreateOrUpdate {
    Create,
    Update
}

class IssuePersistTaskFinished(val issue: Issue) : ApiCallSucceeded()

class IssuePersistTaskFailed(error: Error?) : ApiCallFailed(error)