package io.mangel.issuemanager.api

import java.util.*

abstract class Response

data class CreateTrialAccountResponse(val trialUser: TrialUser) : Response()
data class LoginResponse(val user: User) : Response()
data class ReadResponse(
    val changedCraftsmen: List<Craftsman>,
    val removedCraftsmanIDs: List<UUID>,
    val changedConstructionSites: List<ConstructionSite>,
    val removedConstructionSiteIDs: List<UUID>,
    val changedMaps: List<Map>,
    val removedMapIDs: List<UUID>,
    val changedIssues: List<Issue>,
    val removedIssueIDs: List<UUID>,
    val changedUser: User?
): Response()

data class IssueResponse(val issue: Issue): Response()

data class BinaryResponse(val isSuccessful: Boolean, val error: Error? = null)