package io.mangel.issuemanager.api.tasks

import android.os.AsyncTask
import io.mangel.issuemanager.api.*
import io.mangel.issuemanager.events.ProgressTaskProgressEvent
import io.mangel.issuemanager.events.ProgressTaskStartedEvent
import io.mangel.issuemanager.events.TaskFinishedEvent
import org.greenrobot.eventbus.EventBus
import java.util.*


class FileDownloadTask(private val client: Client) :
    AbstractProgressAsyncTask<FileDownloadTaskEntry, FileDownloadTask.DownloadResult>() {
    override fun execute(request: FileDownloadTaskEntry): DownloadResult {
        val response = client.fileDownload(request.fileDownloadRequest, request.filePath)
        return DownloadResult(request.filePath, response)
    }

    override fun onExecutionFinished(result: DownloadResult) {
        val event = if (result.response != null && result.response.isSuccessful) {
            FileDownloadFinished(result.fileName)
        } else {
            FileDownloadFailed(result.fileName, result.response?.error)
        }
        EventBus.getDefault().post(event)
    }

    class DownloadResult(val fileName: String, val response: ApiResponse<Response>?)
}


class FileDownloadTaskEntry(val fileDownloadRequest: FileDownloadRequest, val filePath: String)

class FileDownloadFinished(val fileName: String) : ApiCallSucceeded()

class FileDownloadFailed(val fileName: String, error: Error?) : ApiCallFailed(error)