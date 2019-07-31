package io.mangel.issuemanager.services.data.store

sealed class SqliteEntry(
    val id: String,
    val lastChangeTime: String
)

class ConstructionSite(
    id: String,
    val name: String,
    val streetAddress: String?,
    val postalCode: String?,
    val locality: String?,
    val country: String?,
    val imagePath: String?,
    lastChangeTime: String
) :
    SqliteEntry(id, lastChangeTime)

class Craftsman(
    id: String,
    val constructionSiteId: String,
    val name: String,
    val trade: String,
    lastChangeTime: String
) :
    SqliteEntry(id, lastChangeTime)

class Map(
    id: String,
    val constructionSiteId: String,
    val parentId: String?,
    val name: String,
    val filePath: String?,
    lastChangeTime: String
) :
    SqliteEntry(id, lastChangeTime)

class Issue(
    id: String,
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
    lastChangeTime: String
) :
    SqliteEntry(id, lastChangeTime)

data class User(val id: String, val lastChangeTime: String, val givenName: String, val familyName: String)
data class AuthenticationToken(val host: String, val authenticationToken: String, val userID: String)