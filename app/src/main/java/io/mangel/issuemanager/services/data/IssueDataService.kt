package io.mangel.issuemanager.services.data

import io.mangel.issuemanager.api.ObjectMeta
import io.mangel.issuemanager.store.Issue
import org.jetbrains.anko.db.RowParser

class IssueDataService(private val sqliteService: SqliteService) : AbstractDataService<Issue>(sqliteService) {
    override val classOfT = Issue::class.java

    fun getIssueImages(): List<IssueImage> {
        return sqliteService.getAllWithNonNullFieldAsCustom(
            Issue::class.java,
            IssueImageRowParser(),
            Issue::imagePath.name,
            Issue::id.name,
            Issue::lastChangeTime.name
        )
    }

    class IssueImage(val imagePath: String, val meta: ObjectMeta)

    class IssueImageRowParser : RowParser<IssueImage> {
        override fun parseRow(columns: Array<Any?>): IssueImage {
            return IssueImage(columns[0] as String, ObjectMeta(columns[1] as String, columns[2] as String))
        }

    }
}