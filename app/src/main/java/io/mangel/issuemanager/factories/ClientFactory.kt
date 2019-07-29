package io.mangel.issuemanager.factories

import io.mangel.issuemanager.api.Client
import io.mangel.issuemanager.services.RestHttpService
import io.mangel.issuemanager.services.SerializationService

class ClientFactory(
    private val httpService: RestHttpService,
    private val serializationService: SerializationService
) {
    fun getClient(host: String): Client {
        return Client(host, httpService, serializationService)
    }
}