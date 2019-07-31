package io.mangel.issuemanager.api

abstract class Response

data class CreateTrialAccountResponse(val trialUser: TrialUser) : Response()
data class LoginResponse(val user: User) : Response()
data class ReadResponse(
    val changedCraftsmen: List<Craftsman>,
    val removedCraftsmanIDs: List<String>,
    val changedConstructionSites: List<ConstructionSite>,
    val removedConstructionSiteIDs: List<String>,
    val changedMaps: List<Map>,
    val removedMapIDs: List<String>,
    val changedIssues: List<Issue>,
    val removedIssueIDs: List<String>,
    val changedUser: User?
): Response()

data class IssueResponse(val issue: Issue): Response()