package io.mangel.issuemanager.data.api

import io.mangel.issuemanager.services.RestHttpService
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types


class Client(private val httpService: RestHttpService, private val baseUrl: String) {
    companion object {
        const val PREFIX = "/api"
    }

    fun createTrialAccount(trialRequest: TrialRequest): Response<TrialUser>? {
        val requestJson = serializeToJson(trialRequest, TrialRequest::class.java)
        val responseJson = httpService.postJson("$baseUrl$PREFIX/trial/create_account", requestJson) ?: return null
        return deserializeFromJson(responseJson, TrialUser::class.java)
    }

    private fun <T1> deserializeFromJson(json: String, parameterT: Class<T1>): Response<T1> {
        val moshi = Moshi.Builder().build()
        val listOfT = Types.newParameterizedType(Response::class.java, parameterT)
        val jsonAdapter = moshi.adapter<Response<T1>>(listOfT)

        return jsonAdapter.fromJson(json)!!
    }

    private fun <T> serializeToJson(trialRequest: T, classOfT: Class<T>): String {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(classOfT)

        return jsonAdapter.toJson(trialRequest)
    }
}