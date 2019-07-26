package io.mangel.issuemanager.api

import io.mangel.issuemanager.services.RestHttpService
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type


class Client(private val httpService: RestHttpService, private val host: String) {
    companion object {
        const val STATUS_SUCCESS = "success"
    }

    private val baseUrl = "$host/api/external"

    fun getDomainOverrides(): ApiResponse<DomainOverrideRoot> {
        val response = httpService.get("$baseUrl/config/domain_overrides")
        val domainRoot = deserialize(response, DomainOverrideRoot::class.java)

        return ApiResponse(domainRoot != null, domainRoot)
    }

    fun createTrialAccount(trialRequest: CreateTrialAccountRequest): ApiResponse<CreateTrialAccountResponse>? {
        val requestJson = serialize(trialRequest)
        val response = httpService.postJsonForString("$baseUrl/trial/create_account", requestJson)

        return deserializeRoot(response, CreateTrialAccountResponse::class.java)
    }

    fun login(request: LoginRequest): ApiResponse<LoginResponse>? {
        val requestJson = serialize(request)
        val response = httpService.postJsonForString("$baseUrl/login", requestJson)

        return deserializeRoot(response, LoginResponse::class.java)
    }

    fun read(readRequest: ReadRequest): ApiResponse<ReadResponse>? {
        val requestJson = serialize(readRequest)
        val response = httpService.postJsonForString("$baseUrl/read", requestJson)

        return deserializeRoot(response, ReadResponse::class.java)
    }

    fun fileDownload(fileDownloadRequest: FileDownloadRequest, filePath: String): ApiResponse<Response>? {
        val requestJson = serialize(fileDownloadRequest)
        val response = httpService.postJsonForFile("$baseUrl/file/download", requestJson, filePath) ?: return null

        return if (response.isSuccessful) {
            ApiResponse(true);
        } else {
            deserializeRoot(response, Response::class.java)
        }
    }

    fun issueCreate(issueRequest: IssueRequest, filePath: String?, fileName: String?): ApiResponse<IssueResponse>? {
        return issueRequest(issueRequest, filePath, fileName, "/issue/create")
    }

    fun issueUpdate(issueRequest: IssueRequest, filePath: String?, fileName: String?): ApiResponse<IssueResponse>? {
        return issueRequest(issueRequest, filePath, fileName, "/issue/update")
    }

    fun issueDelete(issueIDRequest: IssueIDRequest): ApiResponse<Response>? {
        return issueIDRequestWithEmptyResponse(issueIDRequest, "/issue/delete")
    }

    fun issueMark(issueIDRequest: IssueIDRequest): ApiResponse<IssueResponse>? {
        return issueIDRequest(issueIDRequest, "/issue/mark")
    }

    fun issueReview(issueIDRequest: IssueIDRequest): ApiResponse<IssueResponse>? {
        return issueIDRequest(issueIDRequest, "/issue/review")
    }

    fun issueRevert(issueIDRequest: IssueIDRequest): ApiResponse<IssueResponse>? {
        return issueIDRequest(issueIDRequest, "/issue/revert")
    }

    private fun issueRequest(
        issueRequest: IssueRequest,
        filePath: String?,
        fileName: String?,
        url: String
    ): ApiResponse<IssueResponse>? {
        val requestJson = serialize(issueRequest)
        val requestUrl = "$baseUrl$url"

        val response = if (filePath != null && fileName != null) {
            httpService.postJsonAndImageForString(requestUrl, requestJson, filePath, fileName) ?: return null
        } else {
            httpService.postJsonForString(requestUrl, requestJson) ?: return null
        }

        return deserializeRoot(response, IssueResponse::class.java)
    }

    private fun issueIDRequestWithEmptyResponse(issueIDRequest: IssueIDRequest, url: String): ApiResponse<Response>? {
        val requestJson = serialize(issueIDRequest)
        val response = httpService.postJsonForString("$baseUrl$url", requestJson) ?: return null

        return deserializeRoot(response, Response::class.java)
    }

    private fun issueIDRequest(issueIDRequest: IssueIDRequest, url: String): ApiResponse<IssueResponse>? {
        val requestJson = serialize(issueIDRequest)
        val response = httpService.postJsonForString("$baseUrl$url", requestJson) ?: return null

        return deserializeRoot(response, IssueResponse::class.java)
    }

    private fun <T1 : Response> deserializeRoot(
        stringResponse: RestHttpService.StringResponse?,
        parameterT: Class<T1>
    ): ApiResponse<T1>? {
        if (stringResponse == null) {
            return null
        }

        return deserializeRoot(stringResponse.body, parameterT)
    }

    private fun <T1 : Response> deserializeRoot(
        fileResponse: RestHttpService.FileResponse?,
        parameterT: Class<T1>
    ): ApiResponse<T1>? {
        if (fileResponse == null) {
            return null
        }

        return deserializeRoot(fileResponse.errorBody, parameterT)
    }

    private fun <T1 : Response> deserializeRoot(json: String?, parameterT: Class<T1>): ApiResponse<T1>? {
        val rootType = Types.newParameterizedType(Root::class.java, parameterT);
        val root = deserializeType<Root<T1>>(json, rootType) ?: return ApiResponse(false)

        return ApiResponse(root.status == STATUS_SUCCESS, root.data, Error.tryParseFrom(root.error))
    }

    private fun <T1> deserialize(stringResponse: RestHttpService.StringResponse?, classOfT: Class<T1>): T1? {
        if (stringResponse == null) {
            return null
        }

        return deserialize(stringResponse.body, classOfT)
    }

    private fun <T1> deserialize(json: String?, classOfT: Class<T1>): T1? {
        return deserializeType(json, classOfT)
    }

    private fun <T1> deserializeType(json: String?, typeOfT: Type): T1? {
        if (json == null) {
            return null
        }

        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<T1>(typeOfT)

        return jsonAdapter.fromJson(json)
    }

    private fun <T : Request> serialize(request: T): String {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(request.javaClass)

        return jsonAdapter.toJson(request)
    }
}

data class ApiResponse<T>(
    val isSuccessful: Boolean,
    val data: T? = null,
    val error: Error? = null
)