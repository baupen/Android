package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.*
import org.greenrobot.eventbus.EventBus


abstract class AbstractApiAsyncTask<T, T2>(private val client: Client) :
    AbstractProgressAsyncTask<T, ApiResponse<T2>?>() {

    protected abstract fun callApi(request: T, client: Client): ApiResponse<T2>?

    protected abstract fun onExecutionFailed(error: Error?): ApiCallFailed

    protected abstract fun onExecutionSuccessful(response: T2): ApiCallSucceeded

    override fun execute(request: T): ApiResponse<T2>? {
        return callApi(request, client)
    }

    override fun onExecutionFinished(result: ApiResponse<T2>?) {
        val apiCallFinished: ApiCallFinished = if (result == null || !result.isSuccessful || result.data == null) {
            onExecutionFailed(result?.error)
        } else {
            onExecutionSuccessful(result.data)
        }

        EventBus.getDefault().post(apiCallFinished)
    }
}

abstract class ApiCallFinished

abstract class ApiCallSucceeded : ApiCallFinished()
abstract class ApiCallFailed(val error: Error?) : ApiCallFinished()