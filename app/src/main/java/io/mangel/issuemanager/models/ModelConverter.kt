package io.mangel.issuemanager.models

import io.mangel.issuemanager.store.User

class ModelConverter {
    fun convert(user: User): io.mangel.issuemanager.models.User {
        return User(user.givenName, user.familyName)
    }
}