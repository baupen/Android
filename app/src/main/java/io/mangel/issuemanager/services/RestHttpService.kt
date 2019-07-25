package io.mangel.issuemanager.services

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.net.UnknownHostException

class RestHttpService(private val notificationService: NotificationService) {
    companion object {
        private val MEDIA_TYPE_JSON = ("application/json; charset=utf-8").toMediaType()
        private val MEDIA_TYPE_IMAGE = ("image/json; charset=utf-8").toMediaType()
    }

    private var client = OkHttpClient()

    fun get(url: String): String? {
        val request = getRequestBuilder(url)
            .get()
            .build()

        return executeForString(request)
    }

    fun postJsonForString(url: String, json: String): String? {
        val body = json.toRequestBody(MEDIA_TYPE_JSON)
        val request = getRequestBuilder(url)
            .post(body)
            .build()

        return executeForString(request)
    }

    fun postJson(url: String, json: String): Response? {
        val body = json.toRequestBody(MEDIA_TYPE_JSON)
        val request = getRequestBuilder(url)
            .post(body)
            .build()

        return execute(request)
    }

    fun postJsonAndImage(url: String, filePath: String): String? {

        //continue: https://stackoverflow.com/questions/24279563/uploading-a-large-file-in-multipart-using-okhttp
        val file = File(filePath)
        val request = getRequestBuilder(url)
            .post(file.asRequestBody(MEDIA_TYPE_IMAGE))
            .build()

        return executeForString(request)
    }

    private fun getRequestBuilder(url: String): Request.Builder {
        return Request.Builder()
            .url(url)
    }

    private fun executeForString(request: Request): String? {
        return execute(request)?.body?.string()
    }

    private fun execute(request: Request): Response? {
        try {
            client.newCall(request).execute().use { response ->
                return response
            }
        } catch (exception: UnknownHostException) {
            notificationService.showNotification(Notification.NO_INTERNET_ACCESS)
            return null
        } catch (exception: IOException) {
            notificationService.showNotification(Notification.REQUEST_FAILED)
            return null
        }
    }
}