package io.mangel.issuemanager.store

import io.mangel.issuemanager.services.SqliteService


abstract class Meta(id: String, lastChangeTime: String)

class User(val id: String, val lastChangeTime: String, val givenName: String, val familyName: String) :
    Meta(id, lastChangeTime) {
}

data class AuthenticationToken(val host: String, val authenticationToken: String, val userID: String)