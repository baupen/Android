package io.mangel.issuemanager.models

import io.mangel.issuemanager.store.User
import io.mangel.issuemanager.store.ConstructionSite

class ModelConverter {
    fun convert(user: User): io.mangel.issuemanager.models.User {
        return User(user.givenName, user.familyName)
    }

    fun convert(constructionSite: ConstructionSite): io.mangel.issuemanager.models.ConstructionSite {
        val address = Address(constructionSite.streetAddress, constructionSite.postalCode, constructionSite.locality, constructionSite.country)

        return ConstructionSite(constructionSite.name, address, constructionSite.imagePath)
    }
}