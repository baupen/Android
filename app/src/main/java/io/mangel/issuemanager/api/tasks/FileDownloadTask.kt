package io.mangel.issuemanager.api.tasks

import android.os.AsyncTask
import io.mangel.issuemanager.api.*
import io.mangel.issuemanager.events.ProgressTaskProgressEvent
import io.mangel.issuemanager.events.ProgressTaskStartedEvent
import io.mangel.issuemanager.events.TaskFinishedEvent
import org.greenrobot.eventbus.EventBus
import java.util.*


class FileDownloadTask(private val client: Client) : AsyncTask<FileDownloadTaskEntry, FileDownloadTask.ProgressUpdate, Unit>() {
    override fun doInBackground(vararg entries: FileDownloadTaskEntry) {
        for ((current, entry) in entries.withIndex()) {
            val response = client.fileDownload(entry.fileDownloadRequest, entry.filePath)

            if (isCancelled) return
            publishProgress(ProgressUpdate(entry.filePath, response, entries.size, current))
        }
    }

    private val asyncTaskId: UUID = UUID.randomUUID()

    override fun onPreExecute() {
        super.onPreExecute()
        EventBus.getDefault().post(ProgressTaskStartedEvent(asyncTaskId))
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)
        EventBus.getDefault().post(TaskFinishedEvent(asyncTaskId))
    }

    override fun onProgressUpdate(vararg values: ProgressUpdate) {
        super.onProgressUpdate(*values)

        for (value in values) {
            EventBus.getDefault().post(ProgressTaskProgressEvent(asyncTaskId, value.progress, value.max))
            if (value.apiResponse != null && value.apiResponse.isSuccessful) {
                EventBus.getDefault().post(FileDownloadFinished(value.fileName))
            } else {
                EventBus.getDefault().post(FileDownloadFailed(value.fileName, value.apiResponse?.error))
            }
        }
    }


    class ProgressUpdate(val fileName: String, val apiResponse: ApiResponse<Response>?, val progress: Int, val max: Int)
}


class FileDownloadTaskEntry(val fileDownloadRequest: FileDownloadRequest, val filePath: String)

class FileDownloadFinished(val fileName: String)

class FileDownloadFailed(val fileName: String, error: Error?)