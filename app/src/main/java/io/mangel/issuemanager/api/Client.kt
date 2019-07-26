package io.mangel.issuemanager.api

import io.mangel.issuemanager.services.RestHttpService
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types


class Client(private val httpService: RestHttpService, private val host: String) {
    private val baseUrl = "$host/api/external"

    fun getDomainOverrides(): List<DomainOverride> {
        val response = httpService.get("$baseUrl/config/domain_overrides")
        val responseJson = getContentString(response) ?: return ArrayList()
        return deserialize(responseJson, DomainOverrideRoot::class.java).domainOverrides
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

    private fun getContentString(stringResponse: RestHttpService.StringResponse?): String? {
        if (stringResponse != null && stringResponse.isSuccessful) {
            return stringResponse.body
        }
        return null
    }

    fun fileDownload(fileDownloadRequest: FileDownloadRequest, filePath: String): BinaryResponse? {
        val requestJson = serialize(fileDownloadRequest)
        val response = httpService.postJsonForFile("$baseUrl/file/download", requestJson, filePath) ?: return null
        if (response.isSuccessful) {
            return BinaryResponse(true);
        } else {
            var error: Error? = null
            if (response.errorBody != null) {
                val apiResponse = deserializeApiResponse(response, Object::class.java)
                error = Error.tryParseFrom(apiResponse?.error)
            }
            return BinaryResponse(false, error)
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

    private fun <T1> deserializeApiResponse(
        stringResponse: RestHttpService.StringResponse?,
        parameterT: Class<T1>
    ): ApiResponse<T1>? {
        if (stringResponse == null) {
            return null
        }

        return deserializeApiResponse(stringResponse.body, parameterT)
    }

    private fun <T1> deserializeApiResponse(
        fileResponse: RestHttpService.FileResponse?,
        parameterT: Class<T1>
    ): ApiResponse<T1>? {
        if (fileResponse == null) {
            return null
        }

        return deserializeApiResponse(fileResponse.errorBody, parameterT)
    }

    private fun <T1> deserializeApiResponse(json: String?, parameterT: Class<T1>): ApiResponse<T1>? {
        if (json == null) {
            return null
        }

        val moshi = Moshi.Builder().build()
        val listOfT = Types.newParameterizedType(ApiResponse::class.java, parameterT)
        val jsonAdapter = moshi.adapter<ApiResponse<T1>>(listOfT)

        return jsonAdapter.fromJson(json)
    }

    private fun <T1> deserialize(json: String, classOfT: Class<T1>): T1 {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<T1>(classOfT)

        return jsonAdapter.fromJson(json)!!
    }

    private fun <T : Request> serialize(trialRequest: T): String {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(trialRequest.javaClass)

        return jsonAdapter.toJson(trialRequest)
    }
}