package io.mangel.issuemanager.api

import io.mangel.issuemanager.services.RestHttpService
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types


class Client(private val httpService: RestHttpService, private val host: String) {
    private val baseUrl = "$host/api/external"

    fun getDomainOverrides(): List<DomainOverride> {
        val responseJson = httpService.get("$baseUrl/config/domain_overrides") ?: return ArrayList()
        return deserialize(responseJson, DomainOverrideRoot::class.java).domainOverrides
    }

    fun createTrialAccount(trialRequest: CreateTrialAccountRequest): ApiResponse<CreateTrialAccountResponse>? {
        val requestJson = serialize(trialRequest)
        val responseJson = httpService.postJsonForString("$baseUrl/trial/create_account", requestJson) ?: return null
        return deserializeResponse(responseJson, CreateTrialAccountResponse::class.java)
    }

    fun login(request: LoginRequest): ApiResponse<LoginResponse>? {
        val requestJson = serialize(request)
        val responseJson = httpService.postJsonForString("$baseUrl/login", requestJson) ?: return null
        return deserializeResponse(responseJson, LoginResponse::class.java)
    }

    fun read(readRequest: ReadRequest): ApiResponse<ReadResponse>? {
        val requestJson = serialize(readRequest)
        val responseJson = httpService.postJsonForString("$baseUrl/read", requestJson) ?: return null
        return deserializeResponse(responseJson, ReadResponse::class.java)
    }

    fun fileDownload(fileDownloadRequest: FileDownloadRequest): BinaryResponse? {
        // maybe better to save file in rest http service? then do not need to expose response into the client
        val requestJson = serialize(fileDownloadRequest)
        val response = httpService.postJson("$baseUrl/file/download", requestJson) ?: return null

        val body = response.body
        if (body != null) {
            if (response.isSuccessful) {
                return BinaryResponse(true);
            } else {
                val apiResponse = deserializeResponse(body.string(), Object::class.java)
                return BinaryResponse(false, Error.tryParseFrom(apiResponse.error))
            }
        } else {
            return null
        }
    }

    fun issueCreate(issueRequest: IssueRequest, filePath: String?): ApiResponse<IssueResponse>? {
        return issueRequest(issueRequest, filePath, "/issue/create")
    }

    fun issueUpdate(issueRequest: IssueRequest, filePath: String?): ApiResponse<IssueResponse>? {
        return issueRequest(issueRequest, filePath, "/issue/update")
    }

    private fun issueRequest(issueRequest: IssueRequest, filePath: String?, url: String): ApiResponse<IssueResponse>? {
        val requestJson = serialize(issueRequest)
        // todo: pass file path
        val responseJson = httpService.postJsonAndImage("$baseUrl$url", requestJson) ?: return null
        return deserializeResponse(responseJson, IssueResponse::class.java)
    }

    private fun <T1> deserializeResponse(json: String, parameterT: Class<T1>): ApiResponse<T1> {
        val moshi = Moshi.Builder().build()
        val listOfT = Types.newParameterizedType(ApiResponse::class.java, parameterT)
        val jsonAdapter = moshi.adapter<ApiResponse<T1>>(listOfT)

        return jsonAdapter.fromJson(json)!!
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