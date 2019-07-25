package io.mangel.issuemanager.data.api

abstract class Request

data class TrialRequest(val proposedGivenName: String? = null, val proposedFamilyName: String? = null): Request()