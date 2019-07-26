package io.mangel.issuemanager.api.tasks

import io.mangel.issuemanager.api.*
import io.mangel.issuemanager.api.Map
import java.util.*


class ReadTask(client: Client) : AbstractApiAsyncTask<ReadRequest, ReadResponse>(client) {
    override fun callApi(request: ReadRequest, client: Client): ApiResponse<ReadResponse>? {
        return client.read(request)
    }

    override fun onExecutionSuccessful(response: ReadResponse): ApiCallSucceeded {
        return ReadTaskFinished(
            response.changedCraftsmen,
            response.removedCraftsmanIDs,
            response.changedConstructionSites,
            response.removedConstructionSiteIDs,
            response.changedMaps,
            response.removedMapIDs,
            response.changedIssues,
            response.removedIssueIDs,
            response.changedUser
        )
    }

    override fun onExecutionFailed(error: Error?): ApiCallFailed {
        return ReadTaskFailed(error)
    }
}

class ReadTaskFinished(
    val changedCraftsmen: List<Craftsman>,
    val removedCraftsmanIDs: List<UUID>,
    val changedConstructionSites: List<ConstructionSite>,
    val removedConstructionSiteIDs: List<UUID>,
    val changedMaps: List<Map>,
    val removedMapIDs: List<UUID>,
    val changedIssues: List<Issue>,
    val removedIssueIDs: List<UUID>,
    val changedUser: User?
) : ApiCallSucceeded()

class ReadTaskFailed(error: Error?) : ApiCallFailed(error)