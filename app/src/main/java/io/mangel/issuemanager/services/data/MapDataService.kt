package io.mangel.issuemanager.services.data

import io.mangel.issuemanager.api.ObjectMeta
import io.mangel.issuemanager.store.Map
import org.jetbrains.anko.db.RowParser

class MapDataService(private val sqliteService: SqliteService) : AbstractDataService<Map>(sqliteService) {
    override val classOfT = Map::class.java

    fun getMapImages(): List<MapFile> {
        return sqliteService.getAllWithNonNullFieldAsCustom(
            Map::class.java,
            MapFileRowParser(),
            Map::filePath.name,
            Map::id.name,
            Map::lastChangeTime.name
        )
    }

    class MapFile(val filePath: String, val meta: ObjectMeta)

    class MapFileRowParser : RowParser<MapFile> {
        override fun parseRow(columns: Array<Any?>): MapFile {
            return MapFile(columns[0] as String, ObjectMeta(columns[1] as String, columns[2] as String))
        }

    }
}