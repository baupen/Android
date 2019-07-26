package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.*
import org.greenrobot.eventbus.EventBus
import java.util.*


class IssueDeleteTask(private val client: Client) :
    AbstractProgressAsyncTask<IssueIDRequest, IssueDeleteTask.DeleteResult>() {
    override fun execute(request: IssueIDRequest): DeleteResult {
        val response = client.issueDelete(request)
        return DeleteResult(request.issueID, response)
    }

    override fun onExecutionFinished(result: DeleteResult) {
        val event = if (result.response != null && result.response.isSuccessful) {
            IssueDeleteTaskFinished(result.issueID)
        } else {
            IssueDeleteTaskFailed(result.issueID, result.response?.error)
        }
        EventBus.getDefault().post(event)
    }

    class DeleteResult(val issueID: UUID, val response: ApiResponse<Response>?)
}

class IssueDeleteTaskFinished(val issueID: UUID) : ApiCallSucceeded()

class IssueDeleteTaskFailed(val issueID: UUID, error: Error?) : ApiCallFailed(error)