package io.mangel.issuemanager.store

abstract class SqliteEntry<T>(id: String, lastChangeTime: String) {
    abstract fun getMeta() : Meta<T>
}

class User(val id: String, val lastChangeTime: String, val givenName: String, val familyName: String) : SqliteEntry<User>(id, lastChangeTime) {
    override fun getMeta(): Meta<User> {
        return UserMeta()
    }

}

data class AuthenticationToken(val host: String, val authenticationToken: String, val userID: String)