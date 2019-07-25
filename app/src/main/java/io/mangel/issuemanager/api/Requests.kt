package io.mangel.issuemanager.api

import java.util.*

abstract class Request

abstract class AuthenticatedRequest(authenticationToken: String): Request()

data class CreateTrialAccountRequest(val proposedGivenName: String? = null, val proposedFamilyName: String? = null): Request()
data class LoginRequest(val username: String, val passwordHash: String): Request()
data class ReadRequest(val authenticationToken: String, val user: ObjectMeta, val craftsmen: List<ObjectMeta>, val constructionSites: List<ObjectMeta>, val maps: List<ObjectMeta>, val issues: List<ObjectMeta>): AuthenticatedRequest(authenticationToken)
data class FileDownloadRequest(val authenticationToken: String, val constructionSite: ObjectMeta?, val map: ObjectMeta?, val issue: ObjectMeta?): AuthenticatedRequest(authenticationToken)
data class IssueRequest(val authenticationToken: String, val issue: Issue): AuthenticatedRequest(authenticationToken)
data class IssueIDRequest(val authenticationToken: String, val issueID: UUID): AuthenticatedRequest(authenticationToken)