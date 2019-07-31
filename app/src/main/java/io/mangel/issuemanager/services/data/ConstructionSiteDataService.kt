package io.mangel.issuemanager.services.data

import io.mangel.issuemanager.api.ObjectMeta
import io.mangel.issuemanager.services.data.store.ConstructionSite
import org.jetbrains.anko.db.RowParser

class ConstructionSiteDataService(private val sqliteService: SqliteService) :
    AbstractDataService<ConstructionSite>(sqliteService) {
    override val classOfT = ConstructionSite::class.java

    fun getConstructionSiteImages(): List<ConstructionSiteImage> {
        return sqliteService.getAllWithNonNullFieldAsCustom(
            ConstructionSite::class.java,
            ConstructionSiteImageRowParser(),
            ConstructionSite::imagePath.name,
            ConstructionSite::id.name,
            ConstructionSite::lastChangeTime.name
        )
    }

    class ConstructionSiteImage(val imagePath: String, val meta: ObjectMeta)

    class ConstructionSiteImageRowParser : RowParser<ConstructionSiteImage> {
        override fun parseRow(columns: Array<Any?>): ConstructionSiteImage {
            return ConstructionSiteImage(columns[0] as String, ObjectMeta(columns[1] as String, columns[2] as String))
        }

    }
}