package io.mangel.issuemanager.factories

import io.mangel.issuemanager.api.Client
import io.mangel.issuemanager.services.RestHttpService
import io.mangel.issuemanager.services.SerializationService

/**
 * makes constructing the clients easier
 * avoids injecting lower-level services in places where they are not needed (such as repositories which do not need the serializationService)
 */
class ClientFactory(
    private val httpService: RestHttpService,
    private val serializationService: SerializationService
) {
    fun getClient(host: String): Client {
        return Client(host, httpService, serializationService)
    }
}