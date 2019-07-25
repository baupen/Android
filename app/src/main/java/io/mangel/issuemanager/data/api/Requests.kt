package io.mangel.issuemanager.data.api

abstract class Request

data class CreateTrialAccountRequest(val proposedGivenName: String? = null, val proposedFamilyName: String? = null): Request()
data class LoginRequest(val username: String, val passwordHash: String): Request()