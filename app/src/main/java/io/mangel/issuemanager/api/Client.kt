package io.mangel.issuemanager.api

import io.mangel.issuemanager.services.RestHttpService
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types


class Client(private val httpService: RestHttpService, private val host: String) {
    private val baseUrl = "$host/api"

    fun getDomainOverrides(): List<DomainOverride> {
        val responseJson = httpService.get("$baseUrl/external/config/domain_overrides") ?: return ArrayList()
        return deserializeList(responseJson, DomainOverride::class.java)
    }

    fun createTrialAccount(trialRequest: CreateTrialAccountRequest): Response<TrialUser>? {
        val requestJson = serialize(trialRequest)
        val responseJson = httpService.postJson("$baseUrl/trial/create_account", requestJson) ?: return null
        return deserializeResponse(responseJson, TrialUser::class.java)
    }

    fun login(request: LoginRequest): Response<User>? {
        val requestJson = serialize(request)
        val responseJson = httpService.postJson("$baseUrl/login", requestJson) ?: return null
        return deserializeResponse(responseJson, User::class.java)
    }

    private fun <T1> deserializeResponse(json: String, parameterT: Class<T1>): Response<T1> {
        val moshi = Moshi.Builder().build()
        val listOfT = Types.newParameterizedType(Response::class.java, parameterT)
        val jsonAdapter = moshi.adapter<Response<T1>>(listOfT)

        return jsonAdapter.fromJson(json)!!
    }

    private fun <T1> deserializeList(json: String, parameterT: Class<T1>): List<T1> {
        val moshi = Moshi.Builder().build()
        val listOfT = Types.newParameterizedType(List::class.java, parameterT)
        val jsonAdapter = moshi.adapter<List<T1>>(listOfT)

        return jsonAdapter.fromJson(json)!!
    }

    private fun <T: Request> serialize(trialRequest: T): String {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(trialRequest.javaClass)

        return jsonAdapter.toJson(trialRequest)
    }
}