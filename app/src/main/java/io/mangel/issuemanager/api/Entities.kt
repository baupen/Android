package io.mangel.issuemanager.api

import kotlin.collections.Map

data class Root<T>(val version: Int, val status: String, val data: T?, val error: Int?, val message: String?)

enum class Error(val value: Int) {
    InvalidRequest(1), // something in the request was malformed, e.g. missing value in json or missing image in multipart
    InvalidToken(2), // the authentication token is invalid, e.g. because it has expired
    OutdatedClient(3), // the client is outdated, thus the server won't risk communicating with it
    UnknownUsername(100),
    WrongPassword(101),
    IssueAlreadyExists(200),
    IssueNotFound(201),
    OutdatedData(202), // the server ignored the issue change attempt because the client's data is outdated
    InvalidAction(203);  // whatever the client is trying to do is impossible, e.g. reviewing a closed issue

    companion object {
        private val lookupByValue: Map<Int, Error> = values().associateBy { it.value }
        fun tryParseFrom(i: Int?): Error?  {
            if (i == null) {
                return null
            }
            return lookupByValue[i]
        }
    }
}


data class Point(val x: Double, val y: Double)
data class Frame(val startX: Double, val startY: Double, val height: Double, val width: Double)
data class File(val id: String, val filename: String)
data class ObjectMeta(val id: String, val lastChangeTime: String)
data class TrialUser(val username: String, val password: String)
data class User(val meta: ObjectMeta, val authenticationToken: String, val givenName: String, val familyName: String)
data class ConstructionSite(val meta: ObjectMeta, val name: String, val address: Address, val image: File)
data class Address(val streetAddress: String?, val postalCode: Int?, val locality: String?, val country: String?)
data class Craftsman(val meta: ObjectMeta, val name: String, val constructionSiteId: String, val trade: String)
data class Map(val meta: ObjectMeta, val file: File?, val name: String, val constructionSiteId: String, val parentId: String?)
data class Issue(val meta: ObjectMeta, val number: Int?, val isMarked: Boolean, val wasAddedWithClient: Boolean, val image: File?, val description: String?, val craftsman: String?, val map: String, val status: Status, val position: Position?)
data class Position(val point: Point, val zoomScale: Double, val mapFileID: String)
data class Status(val registration: Event?, val response: Event?, val review: Event?)
data class Event(val time: String, val author: String)

data class DomainOverrideRoot(val domainOverrides: List<DomainOverride>)
data class DomainOverride(val userInputDomain: String, val serverURL: String, val userLoginDomain: String)