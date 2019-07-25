package io.mangel.issuemanager.services

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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

        return execute(request)
    }

    fun postJson(url: String, json: String): String? {
        val body = json.toRequestBody(MEDIA_TYPE_JSON)
        val request = getRequestBuilder(url)
            .post(body)
            .build()

        return execute(request)
    }

    fun postImage(url: String, filePath: String): String? {
        val file = File(filePath)
        val request = getRequestBuilder(url)
            .post(file.asRequestBody(MEDIA_TYPE_IMAGE))
            .build()

        return execute(request)
    }

    private fun getRequestBuilder(url: String): Request.Builder {
        return Request.Builder()
            .url(url)
    }

    private fun execute(request: Request): String? {
        try {
            client.newCall(request).execute().use { response ->
                return response.body?.string()
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