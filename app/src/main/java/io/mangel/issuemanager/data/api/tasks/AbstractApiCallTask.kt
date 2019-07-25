package io.mangel.issuemanager.data.api.tasks

import android.os.AsyncTask
import io.mangel.issuemanager.data.api.*
import io.mangel.issuemanager.data.events.TaskFinishedEvent
import io.mangel.issuemanager.data.events.TaskStartedEvent
import org.greenrobot.eventbus.EventBus
import java.util.*


abstract class AbstractApiCallTask<T1, T2>(private val client: Client) : AsyncTask<T1, Int, T2>() {

    protected abstract fun callApi(request: T1, client: Client): T2

    protected abstract fun onExecutionFinished(asyncTaskId: UUID, result: T2): TaskFinishedEvent

    override fun doInBackground(vararg requests: T1): T2 {
        return callApi(requests.first(), client)
    }

    private val asyncTaskId: UUID = UUID.randomUUID()

    override fun onPreExecute() {
        super.onPreExecute()
        EventBus.getDefault().post(TaskStartedEvent(asyncTaskId))
    }

    override fun onPostExecute(result: T2) {
        super.onPostExecute(result)

        val event = onExecutionFinished(asyncTaskId, result)
        EventBus.getDefault().post(event)
    }
}