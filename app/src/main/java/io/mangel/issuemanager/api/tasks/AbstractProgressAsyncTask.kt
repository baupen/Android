package io.mangel.issuemanager.api.tasks

import android.os.AsyncTask
import io.mangel.issuemanager.events.ProgressTaskStartedEvent
import io.mangel.issuemanager.events.TaskFinishedEvent
import io.mangel.issuemanager.events.TaskProgressEvent
import org.greenrobot.eventbus.EventBus
import java.util.*


abstract class AbstractProgressAsyncTask<T1, T2> : AsyncTask<T1, AbstractProgressAsyncTask.ProgressUpdate<T2>, Unit>() {

    protected abstract fun execute(request: T1): T2

    protected abstract fun onExecutionFinished(result: T2)

    override fun doInBackground(vararg requests: T1) {
        for ((current, request) in requests.withIndex()) {
            val response = this.execute(request)

            publishProgress(ProgressUpdate(response, current, requests.size))
            if (isCancelled) return
        }
    }

    private val asyncTaskId: UUID = UUID.randomUUID()

    override fun onPreExecute() {
        super.onPreExecute()
        EventBus.getDefault().post(ProgressTaskStartedEvent(asyncTaskId, this.javaClass))
    }

    override fun onProgressUpdate(vararg values: ProgressUpdate<T2>) {
        super.onProgressUpdate(*values)

        for (progressUpdate in values) {
            onExecutionFinished(progressUpdate.response)

            EventBus.getDefault().post(TaskProgressEvent(asyncTaskId, progressUpdate.progress, progressUpdate.max))
        }
    }

    override fun onPostExecute(result: Unit) {
        super.onPostExecute(result)

        EventBus.getDefault().post(TaskFinishedEvent(asyncTaskId, this.javaClass))
    }

    class ProgressUpdate<T>(val response: T, val progress: Int, val max: Int)

}