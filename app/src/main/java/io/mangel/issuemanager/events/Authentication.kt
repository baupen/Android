package io.mangel.issuemanager.events

import io.mangel.issuemanager.store.AuthenticationToken
import java.util.*

class AuthenticationTokenRefreshed(val authenticationToken: AuthenticationToken)