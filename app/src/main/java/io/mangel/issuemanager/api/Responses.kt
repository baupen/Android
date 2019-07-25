package io.mangel.issuemanager.api

abstract class Response

data class CreateTrialAccountResponse(val trialUser: TrialUser): Response()
data class LoginResponse(val user: User): Response()