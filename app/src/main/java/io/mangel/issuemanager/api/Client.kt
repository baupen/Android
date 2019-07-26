package io.mangel.issuemanager.api

import io.mangel.issuemanager.services.RestHttpService
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types


class Client(private val httpService: RestHttpService, private val host: String) {
    companion object {
        const val STATUS_SUCCESS = "success"
    }

    private val baseUrl = "$host/api/external"

    fun getDomainOverrides(): List<DomainOverride> {
        val response = httpService.get("$baseUrl/config/domain_overrides")
        val domainOverrideRoot = deserialize(response, DomainOverrideRoot::class.java) ?: return ArrayList()

        return domainOverrideRoot.domainOverrides
    }

    fun createTrialAccount(trialRequest: CreateTrialAccountRequest): ApiResponse<CreateTrialAccountResponse>? {
        val requestJson = serialize(trialRequest)
        val response = httpService.postJsonForString("$baseUrl/trial/create_account", requestJson)

        return deserializeApiResponse(response, CreateTrialAccountResponse::class.java)
    }

    fun login(request: LoginRequest): ApiResponse<LoginResponse>? {
        val requestJson = serialize(request)
        val response = httpService.postJsonForString("$baseUrl/login", requestJson)

        return deserializeApiResponse(response, LoginResponse::class.java)
    }

    fun read(readRequest: ReadRequest): ApiResponse<ReadResponse>? {
        val requestJson = serialize(readRequest)
        val response = httpService.postJsonForString("$baseUrl/read", requestJson)

        return deserializeApiResponse(response, ReadResponse::class.java)
    }

    fun fileDownload(fileDownloadRequest: FileDownloadRequest, filePath: String): ApiResponse<Response>? {
        val requestJson = serialize(fileDownloadRequest)
        val response = httpService.postJsonForFile("$baseUrl/file/download", requestJson, filePath) ?: return null

        return if (response.isSuccessful) {
            ApiResponse(true);
        } else {
            deserializeApiResponse(response, Response::class.java)
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

        return deserializeApiResponse(response, IssueResponse::class.java)
    }

    private fun issueIDRequestWithEmptyResponse(issueIDRequest: IssueIDRequest, url: String): ApiResponse<Response>? {
        val requestJson = serialize(issueIDRequest)
        val response = httpService.postJsonForString("$baseUrl$url", requestJson) ?: return null

        return deserializeApiResponse(response, Response::class.java)
    }

    private fun issueIDRequest(issueIDRequest: IssueIDRequest, url: String): ApiResponse<IssueResponse>? {
        val requestJson = serialize(issueIDRequest)
        val response = httpService.postJsonForString("$baseUrl$url", requestJson) ?: return null

        return deserializeApiResponse(response, IssueResponse::class.java)
    }

    private fun <T1 : Response> deserializeApiResponse(
        stringResponse: RestHttpService.StringResponse?,
        parameterT: Class<T1>
    ): ApiResponse<T1>? {
        if (stringResponse == null) {
            return null
        }

        return deserializeApiResponse(stringResponse.body, parameterT)
    }

    private fun <T1 : Response> deserializeApiResponse(
        fileResponse: RestHttpService.FileResponse?,
        parameterT: Class<T1>
    ): ApiResponse<T1>? {
        if (fileResponse == null) {
            return null
        }

        return deserializeApiResponse(fileResponse.errorBody, parameterT)
    }

    private fun <T1 : Response> deserializeApiResponse(json: String?, parameterT: Class<T1>): ApiResponse<T1>? {
        if (json == null) {
            return null
        }

        val moshi = Moshi.Builder().build()
        val listOfT = Types.newParameterizedType(Root::class.java, parameterT)
        val jsonAdapter = moshi.adapter<Root<T1>>(listOfT)

        val root = jsonAdapter.fromJson(json) ?: return ApiResponse(false)

        return ApiResponse(root.status == STATUS_SUCCESS, Error.tryParseFrom(root.error), root.data)
    }

    private fun <T1> deserialize(stringResponse: RestHttpService.StringResponse?, classOfT: Class<T1>): T1? {
        if (stringResponse == null) {
            return null
        }

        return deserialize(stringResponse.body, classOfT)
    }

    private fun <T1> deserialize(json: String?, classOfT: Class<T1>): T1? {
        if (json == null) {
            return null
        }

        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<T1>(classOfT)

        return jsonAdapter.fromJson(json)
    }

    private fun <T : Request> serialize(request: T): String {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(request.javaClass)

        return jsonAdapter.toJson(request)
    }
}

data class ApiResponse<T>(val isSuccessful: Boolean, val error: Error? = null, val data: T? = null)