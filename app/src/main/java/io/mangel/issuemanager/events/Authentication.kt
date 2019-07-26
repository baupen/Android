package io.mangel.issuemanager.events

import io.mangel.issuemanager.store.AuthenticationToken

class AuthenticationSuccessfulEvent(val authenticationToken: AuthenticationToken)