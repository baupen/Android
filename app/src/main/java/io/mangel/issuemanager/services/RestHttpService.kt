package io.mangel.issuemanager.services

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.net.UnknownHostException


class RestHttpService(private val notificationService: NotificationService, private val fileService: FileService) {
    companion object {
        private val MEDIA_TYPE_JSON = ("application/json; charset=utf-8").toMediaType()
        private val MEDIA_TYPE_IMAGE = ("image/jpeg").toMediaType()
    }

    private var client = OkHttpClient()

    fun get(url: String): StringResponse? {
        val request = getRequestBuilder(url)
            .get()
            .build()

        return getStringResponse(request)
    }

    fun postJsonForString(url: String, json: String): StringResponse? {
        val body = json.toRequestBody(MEDIA_TYPE_JSON)
        val request = getRequestBuilder(url)
            .post(body)
            .build()

        return getStringResponse(request)
    }

    fun postJsonForFile(url: String, json: String, filePath: String): FileResponse? {
        val body = json.toRequestBody(MEDIA_TYPE_JSON)
        val request = getRequestBuilder(url)
            .post(body)
            .build()

        return getFileResponse(request, filePath)
    }

    fun postJsonAndImageForString(url: String, json: String, filePath: String, fileName: String): StringResponse? {
        val file = File(filePath)
        val fileRequestBody = file.asRequestBody(MEDIA_TYPE_IMAGE)
        val requestBody = MultipartBody.Builder()
            .addFormDataPart("message", json)
            .addFormDataPart("image", fileName, fileRequestBody)
            .build()

        val request = getRequestBuilder(url)
            .post(requestBody)
            .build()

        return getStringResponse(request)
    }

    private fun getRequestBuilder(url: String): Request.Builder {
        return Request.Builder()
            .url(url)
    }

    private fun getStringResponse(request: Request): StringResponse? {
        val response = execute(request) ?: return null;

        response.use {
            return StringResponse(response.isSuccessful, response.body?.string())
        }
    }

    private fun getFileResponse(request: Request, filePath: String): FileResponse? {
        val response = execute(request) ?: return null;

        response.use {
            var errorBody: String? = null
            val successful = response.isSuccessful
            val body = response.body
            if (body != null) {
                if (successful) {
                    fileService.save(filePath, body.bytes())
                } else {
                    errorBody = body.string()
                }
            }

            return FileResponse(successful, errorBody)
        }
    }

    private fun execute(request: Request): okhttp3.Response? {
        try {
            return client.newCall(request).execute()
        } catch (exception: UnknownHostException) {
            notificationService.showNotification(Notification.NO_INTERNET_ACCESS)
            return null
        } catch (exception: IOException) {
            notificationService.showNotification(Notification.REQUEST_FAILED)
            return null
        }
    }

    open class Response(val isSuccessful: Boolean)
    class FileResponse(successful: Boolean, val errorBody: String? = null) : Response(successful)
    class StringResponse(successful: Boolean, val body: String?) : Response(successful)
}