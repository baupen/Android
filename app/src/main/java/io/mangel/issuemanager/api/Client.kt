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
        val responseJson = httpService.postJson("$baseUrl/trial/create_account", requestJson) ?: return null
        return deserializeResponse(responseJson, CreateTrialAccountResponse::class.java)
    }

    fun login(request: LoginRequest): ApiResponse<LoginResponse>? {
        val requestJson = serialize(request)
        val responseJson = httpService.postJson("$baseUrl/login", requestJson) ?: return null
        return deserializeResponse(responseJson, LoginResponse::class.java)
    }

    private fun <T1> deserializeResponse(json: String, parameterT: Class<T1>): ApiResponse<T1> {
        val moshi = Moshi.Builder().build()
        val listOfT = Types.newParameterizedType(ApiResponse::class.java, parameterT)
        val jsonAdapter = moshi.adapter<ApiResponse<T1>>(listOfT)

        return jsonAdapter.fromJson(json)!!
    }

    private fun <T1> deserializeList(json: String, parameterT: Class<T1>): List<T1> {
        val moshi = Moshi.Builder().build()
        val listOfT = Types.newParameterizedType(List::class.java, parameterT)
        val jsonAdapter = moshi.adapter<List<T1>>(listOfT)

        return jsonAdapter.fromJson(json)!!
    }

    private fun <T1> deserialize(json: String, classOfT: Class<T1>): T1 {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<T1>(classOfT)

        return jsonAdapter.fromJson(json)!!
    }

    private fun <T: Request> serialize(trialRequest: T): String {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(trialRequest.javaClass)

        return jsonAdapter.toJson(trialRequest)
    }
}