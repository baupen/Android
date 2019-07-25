package io.mangel.issuemanager.data.api.tasks

import android.os.AsyncTask
import io.mangel.issuemanager.data.api.Client
import io.mangel.issuemanager.data.api.Error
import io.mangel.issuemanager.data.api.Response
import io.mangel.issuemanager.data.api.TrialRequest
import io.mangel.issuemanager.data.api.TrialUser
import io.mangel.issuemanager.data.events.TaskFinishedEvent
import io.mangel.issuemanager.data.events.TaskStartedEvent
import org.greenrobot.eventbus.EventBus
import java.util.*


class RefreshMensaTask(private val client: Client) : AsyncTask<TrialRequest, Int, Response<TrialUser>?>() {

    companion object {
        const val STATUS_SUCCESS = "success"
        const val STATUS_FAIL = "fail"
        const val STATUS_ERROR = "error"
    }

    private val asyncTaskId = UUID.randomUUID()

    override fun doInBackground(vararg requests: TrialRequest): Response<TrialUser>? {
        return client.createTrialAccount(requests.first())
    }

    override fun onPreExecute() {
        super.onPreExecute()
        EventBus.getDefault().post(TaskStartedEvent(asyncTaskId))
    }

    override fun onPostExecute(result: Response<TrialUser>?) {
        super.onPostExecute(result)

        if (isRequestFailed(result) || result?.data == null) {
            EventBus.getDefault().post(CreateTrialAccountTaskFailed(asyncTaskId, Error.tryParseFrom(result?.error)))
        } else {
            EventBus.getDefault().post(CreateTrialAccountTaskFinished(asyncTaskId, result.data))
        }
    }

    private fun isRequestFailed(result: Response<TrialUser>?) = result == null || result.status != STATUS_SUCCESS
}


class CreateTrialAccountTaskFinished(taskId: UUID, val trialUser: TrialUser) : TaskFinishedEvent(taskId)

class CreateTrialAccountTaskFailed(taskId: UUID, error: Error?) : ApiCallFailed(taskId, error)

abstract class ApiCallFailed(taskId: UUID, val error: Error?) : TaskFinishedEvent(taskId)