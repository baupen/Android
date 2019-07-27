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
    val number: Int? = null,
    val isMarked: Boolean = false,
    val imagePath: String? = null,
    val description: String? = null,
    val craftsmanId: String? = null,
    val registrationTime: String? = null,
    val registrationAuthor: String? = null,
    val responseTime: String? = null,
    val responseAuthor: String? = null,
    val reviewTime: String? = null,
    val reviewAuthor: String? = null,
    val positionX: Double?,
    val positionY: Double?,
    val zoomScale: Double?,
    val mapFileID: UUID?,
    val lastChangeTime: String
) :
    SqliteEntry<User>() {
}

data class User(val id: String, val lastChangeTime: String, val givenName: String, val familyName: String)
data class AuthenticationToken(val host: String, val authenticationToken: String, val userID: String)