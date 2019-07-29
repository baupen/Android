package io.mangel.issuemanager.store

import io.mangel.issuemanager.api.Craftsman
import io.mangel.issuemanager.api.ConstructionSite
import io.mangel.issuemanager.api.Issue
import io.mangel.issuemanager.api.Map
import io.mangel.issuemanager.api.User
import io.mangel.issuemanager.api.File

class StoreConverter {
    fun convert(user: User): io.mangel.issuemanager.store.User {
        return User(user.meta.id, user.meta.lastChangeTime, user.givenName, user.familyName)
    }

    fun getAuthenticationToken(host: String, user: User): AuthenticationToken {
        return AuthenticationToken(host, user.authenticationToken, user.meta.id)
    }

    fun convert(constructionSite: ConstructionSite): io.mangel.issuemanager.store.ConstructionSite {
        val value = if (constructionSite.address.postalCode != null) {
            constructionSite.address.postalCode.toString()
        } else {
            null
        }


        return ConstructionSite(
            constructionSite.meta.id,
            constructionSite.name,
            constructionSite.address.streetAddress,
            value,
            constructionSite.address.locality,
            constructionSite.address.country,
            collisionFreeFilename(constructionSite.image),
            constructionSite.meta.lastChangeTime
        )
    }

    fun convert(map: Map): io.mangel.issuemanager.store.Map {
        return Map(
            map.meta.id,
            map.constructionSiteID,
            map.parentID,
            map.name,
            collisionFreeFilename(map.file),
            map.meta.lastChangeTime
        )
    }

    fun convert(issue: Issue): io.mangel.issuemanager.store.Issue {
        return Issue(
            issue.meta.id,
            issue.map,
            issue.wasAddedWithClient,
            issue.number,
            issue.isMarked,
            collisionFreeFilename(issue.image),
            issue.description,
            issue.craftsman,
            issue.status.registration?.time,
            issue.status.registration?.author,
            issue.status.response?.time,
            issue.status.response?.author,
            issue.status.review?.time,
            issue.status.review?.author,
            issue.position?.point?.x,
            issue.position?.point?.y,
            issue.position?.zoomScale,
            issue.position?.mapFileID,
            issue.meta.lastChangeTime
        )
    }

    fun convert(craftsman: Craftsman): io.mangel.issuemanager.store.Craftsman {
        return Craftsman(
            craftsman.meta.id,
            craftsman.constructionSiteID,
            craftsman.name,
            craftsman.trade,
            craftsman.meta.lastChangeTime
        )
    }

    private fun collisionFreeFilename(file: File?): String? {
        if (file == null) {
            return null
        }

        val filename = file.filename

        return file.id + filename.substring(filename.lastIndexOf("."))
    }
}