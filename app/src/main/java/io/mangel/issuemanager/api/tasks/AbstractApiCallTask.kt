package io.mangel.issuemanager.api.tasks

import android.os.AsyncTask
import io.mangel.issuemanager.api.Client
import io.mangel.issuemanager.events.TaskFinishedEvent
import io.mangel.issuemanager.events.TaskStartedEvent
import org.greenrobot.eventbus.EventBus
import java.util.*


abstract class AbstractApiCallTask<T1, T2>(private val client: Client) : AsyncTask<T1, Int, T2>() {

    protected abstract fun callApi(client: Client, vararg requests: T1): T2

    protected abstract fun onExecutionFinished(result: T2): Any

    override fun doInBackground(vararg requests: T1): T2 {
        return callApi(client, *requests)
    }

    private val asyncTaskId: UUID = UUID.randomUUID()

    override fun onPreExecute() {
        super.onPreExecute()
        EventBus.getDefault().post(TaskStartedEvent(asyncTaskId))
    }

    override fun onPostExecute(result: T2) {
        super.onPostExecute(result)

        val event = onExecutionFinished(result)
        EventBus.getDefault().post(event)

        EventBus.getDefault().post(TaskFinishedEvent(asyncTaskId))
    }
}