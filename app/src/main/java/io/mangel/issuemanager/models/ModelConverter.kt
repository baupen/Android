package io.mangel.issuemanager.models

import io.mangel.issuemanager.services.data.store.ConstructionSite
import io.mangel.issuemanager.services.data.store.Issue
import io.mangel.issuemanager.services.data.store.Map
import io.mangel.issuemanager.services.data.store.User
import io.mangel.issuemanager.services.data.store.Craftsman
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime

class ModelConverter {
    fun convert(user: User): io.mangel.issuemanager.models.User {
        return User(user.givenName, user.familyName)
    }

    fun convert(constructionSite: ConstructionSite): io.mangel.issuemanager.models.ConstructionSite {
        val address = Address(
            constructionSite.streetAddress,
            constructionSite.postalCode,
            constructionSite.locality,
            constructionSite.country
        )

        return ConstructionSite(constructionSite.id, constructionSite.name, address, constructionSite.imagePath)
    }

    fun convert(craftsman: Craftsman): io.mangel.issuemanager.models.Craftsman {
        return Craftsman(craftsman.id, craftsman.name, craftsman.trade)
    }

    fun convert(map: Map, parentMap: io.mangel.issuemanager.models.Map?): io.mangel.issuemanager.models.Map {
        return Map(map.id, parentMap, map.name, map.filePath)
    }

    fun convert(
        issue: Issue,
        map: io.mangel.issuemanager.models.Map,
        craftsman: io.mangel.issuemanager.models.Craftsman?
    ): io.mangel.issuemanager.models.Issue {
        val modelIssue = Issue(issue.id, map, issue.number, issue.wasAddedWithClient)

        modelIssue.craftsman = craftsman
        modelIssue.description = issue.description
        modelIssue.imagePath = issue.imagePath
        modelIssue.isMarked = issue.isMarked

        val position = Position(
            parsePoint(issue.positionX, issue.positionY),
            issue.zoomScale,
            issue.mapFileID
        )
        modelIssue.position = position

        val status = Status(
            parseEvent(issue.registrationTime, issue.registrationAuthor),
            parseEvent(issue.responseTime, issue.responseAuthor),
            parseEvent(issue.reviewTime, issue.reviewAuthor)
        )
        modelIssue.status = status

        return modelIssue
    }

    private fun parseEvent(time: String?, author: String?): Event? {
        if (time == null || author == null) {
            return null
        }

        val date = ZonedDateTime.parse(time).toLocalDateTime()
        return Event(date, author)
    }

    private fun parsePoint(x: Double?, y: Double?): Point? {
        if (x == null || y == null) {
            return null
        }

        return Point(x, y)
    }
}