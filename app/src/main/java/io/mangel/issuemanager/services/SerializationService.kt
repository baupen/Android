package io.mangel.issuemanager.services

import com.squareup.moshi.Moshi
import io.mangel.issuemanager.api.Request
import java.lang.reflect.Type

class SerializationService {
    fun <T1> deserialize(json: String?, classOfT: Class<T1>): T1? {
        return deserialize(json, classOfT as Type)
    }

    fun <T1> deserialize(json: String?, typeOfT: Type): T1? {
        if (json == null) {
            return null
        }

        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<T1>(typeOfT)

        return jsonAdapter.fromJson(json)
    }

    fun <T: Any> serialize(request: T): String {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(request.javaClass)

        return jsonAdapter.toJson(request)
    }
}