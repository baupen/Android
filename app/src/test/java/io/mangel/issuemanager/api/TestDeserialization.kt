package io.mangel.issuemanager.api

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.mangel.issuemanager.data.api.File
import io.mangel.issuemanager.data.api.Response
import io.mangel.issuemanager.data.api.TrialUser
import io.mangel.issuemanager.data.api.User
import org.junit.Test

class TestDeserializationTest {
    @Test
    fun deserializationFile_worksAsExpected() {
        val json =
            """
                {
                    "id": "d80db661-5856-438f-85cf-63064a67a11d",
                    "filename": "some"
                } 
            """;

        val element = jsonToT(json, File::class.java)

        assertThat(element.id).isEqualTo("d80db661-5856-438f-85cf-63064a67a11d")
        assertThat(element.filename).matches("some")
    }

    @Test
    fun deserializationUser_worksAsExpected() {
        val json =
            """
                {
                    "meta": {
                        "id": "someid",
                        "lastChangeTime": "sometime"
                    },
                    "authenticationToken": "someToken",
                    "givenName": "someName",
                    "familyName": "someName"
                } 
            """;

        val element = jsonToT(json, User::class.java)

        assertThat(element.meta.id).isEqualTo("someid")
    }

    @Test
    fun deserializationResponse_worksAsExpected() {
        val json =
            """
                {
                    "version": 1,
                    "status": "fail",
                    "error": 203,
                    "message": "invalid action"
                } 
            """;

        val element = responseJsonToT(json, TrialUser::class.java)

        assertThat(element.error).isEqualTo(203)
    }

    protected fun <T> jsonToT(json: String, classOfT: Class<T>): T {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<T>(classOfT)

        return jsonAdapter.fromJson(json)!!
    }

    protected fun <T1> responseJsonToT(json: String, parameterT: Class<T1>): Response<T1> {
        val moshi = Moshi.Builder().build()
        val listOfT = Types.newParameterizedType(Response::class.java, parameterT)
        val jsonAdapter = moshi.adapter<Response<T1>>(listOfT)

        return jsonAdapter.fromJson(json)!!
    }
}
