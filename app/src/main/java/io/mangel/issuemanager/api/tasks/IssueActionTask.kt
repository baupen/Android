package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.*


class IssueActionTask(client: Client) : AbstractApiAsyncTask<IssueActionPayload, IssueResponse>(client) {
    override fun callApi(request: IssueActionPayload, client: Client): ApiResponse<IssueResponse>? {
        return when (request.action) {
            Action.Mark -> client.issueMark(request.issueIDRequest)
            Action.Review -> client.issueReview(request.issueIDRequest)
            Action.Revert -> client.issueRevert(request.issueIDRequest)
        }
    }

    override fun onExecutionSuccessful(response: IssueResponse): ApiCallSucceeded {
        return IssuePersistTaskFinished(response.issue)
    }

    override fun onExecutionFailed(error: Error?): ApiCallFailed {
        return IssueActionTaskFailed(error)
    }
}

class IssueActionPayload(
    val action: Action,
    val issueIDRequest: IssueIDRequest
)

enum class Action {
    Mark,
    Review,
    Revert
}

class IssueActionTaskFinished(val issue: Issue) : ApiCallSucceeded()

class IssueActionTaskFailed(error: Error?) : ApiCallFailed(error)