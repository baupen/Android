package io.mangel.issuemanager.store

import java.util.*

sealed class SqliteEntry<T>

class ConstructionSite(
    val id: String,
    val name: String,
    val streetAddress: String?,
    val postalCode: String?,
    val locality: String?,
    val country: String?,
    val imagePath: String?,
    val lastChangeTime: String
) :
    SqliteEntry<User>() {
}

class Craftsman(
    val id: String,
    val constructionSiteId: String,
    val name: String,
    val trade: String,
    val lastChangeTime: String
) :
    SqliteEntry<User>() {
}

class Map(
    val id: String,
    val constructionSiteId: String,
    val parentId: String?,
    val name: String,
    val filePath: String?,
    val lastChangeTime: String
) :
    SqliteEntry<User>() {
}

class Issue(
    val id: String,
    val mapId: String,
    val wasAddedWithClient: Boolean,
    val number: Int?,
    val isMarked: Boolean,
    val imagePath: String?,
    val description: String?,
    val craftsmanId: String?,
    val registrationTime: String?,
    val registrationAuthor: String?,
    val responseTime: String?,
    val responseAuthor: String?,
    val reviewTime: String?,
    val reviewAuthor: String?,
    val positionX: Double?,
    val positionY: Double?,
    val zoomScale: Double?,
    val mapFileID: String?,
    val lastChangeTime: String
) :
    SqliteEntry<User>() {
}

data class User(val id: String, val lastChangeTime: String, val givenName: String, val familyName: String)
data class AuthenticationToken(val host: String, val authenticationToken: String, val userID: String)