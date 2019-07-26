package io.mangel.issuemanager.api

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import org.junit.Test
import com.squareup.moshi.Types


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

        val element = deserializeResponse(json, CreateTrialAccountResponse::class.java)

        assertThat(element.error).isEqualTo(203)
    }

    @Test
    fun deserializationResponseWithGeneric_worksAsExpected() {
        val json =
            """
                {"version":1,"status":"success","data":{"user":{"authenticationToken":"token","givenName":"given","familyName":"family","meta":{"id":"8DBF40EE-218D-40F9-AD5A-79B40FB4EBD0","lastChangeTime":"2019-07-21T13:19:12+02:00"}}},"message":null,"error":null}
            """

        val element = deserializeResponse(json, LoginResponse::class.java)

        assertThat(element.data).isNotNull()
        assertThat(element.data!!.user.familyName).isEqualTo("family")
    }

    private fun <T> jsonToT(json: String, classOfT: Class<T>): T {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<T>(classOfT)

        return jsonAdapter.fromJson(json)!!
    }

    private fun <T1 : Response> deserializeResponse(json: String, parameterT: Class<T1>): Root<T1> {

        val moshi = Moshi.Builder().build()
        val listOfT = Types.newParameterizedType(Root::class.java, parameterT)
        val jsonAdapter = moshi.adapter<Root<T1>>(listOfT)

        return jsonAdapter.fromJson(json)!!
    }
}
