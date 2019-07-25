package io.mangel.issuemanager.data.api.tasks

import android.os.AsyncTask
import io.mangel.issuemanager.data.api.*
import io.mangel.issuemanager.data.events.TaskFinishedEvent
import io.mangel.issuemanager.data.events.TaskStartedEvent
import org.greenrobot.eventbus.EventBus
import java.util.*


abstract class AbstractApiCallTask<T : Request, T2>(private val client: Client) : AsyncTask<T, Int, Response<T2>?>() {

    companion object {
        const val STATUS_SUCCESS = "success"
        const val STATUS_FAIL = "fail"
        const val STATUS_ERROR = "error"
    }

    private val asyncTaskId = UUID.randomUUID()

    protected abstract fun callApi(request: T, client: Client): Response<T2>?

    protected abstract fun onExecutionFailed(asyncTaskId: UUID, error: Error?): ApiCallFailed

    protected abstract fun onExecutionSuccessful(asyncTaskId: UUID, response: T2): TaskFinishedEvent

    override fun doInBackground(vararg requests: T): Response<T2>? {
        return callApi(requests.first(), client)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        EventBus.getDefault().post(TaskStartedEvent(asyncTaskId))
    }

    override fun onPostExecute(result: Response<T2>?) {
        super.onPostExecute(result)

        if (isRequestFailed(result) || result?.data == null) {
            val event = onExecutionFailed(asyncTaskId, Error.tryParseFrom(result?.error))
            EventBus.getDefault().post(event)
        } else {
            val event = onExecutionSuccessful(asyncTaskId, result.data)
            EventBus.getDefault().post(event)
        }
    }

    protected fun isRequestFailed(result: Response<T2>?) = result == null || result.status != STATUS_SUCCESS
}

abstract class ApiCallFailed(taskId: UUID, val error: Error?) : TaskFinishedEvent(taskId)