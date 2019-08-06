package io.mangel.issuemanager.services.data.store

import org.jetbrains.anko.db.*
import java.lang.IllegalArgumentException

class MetaProvider {
    val supported: Array<Class<*>> = arrayOf(
        ConstructionSite::class.java,
        Craftsman::class.java,
        Map::class.java,
        Issue::class.java
    )

    fun <T : Any> getMeta(classOfT: Class<T>): Meta<T> {
        val meta = when (classOfT.name) {
            ConstructionSite::class.java.name -> ConstructionSiteMeta()
            Craftsman::class.java.name -> CraftsmanMeta()
            Map::class.java.name -> MapMeta()
            Issue::class.java.name -> IssueMeta()
            else -> throw IllegalArgumentException("this class is not supported")
        }

        @Suppress("UNCHECKED_CAST")
        return meta as Meta<T>
    }
}


abstract class Meta<T> {
    abstract fun getTableName(): String

    abstract fun getColumns(): Array<Pair<String, SqlType>>

    abstract fun toArray(element: T): Array<Any?>

    protected abstract fun parseRows(): (Array<Any?>) -> T

    private val _fieldList = this.getColumns().map { c -> c.first }

    fun getFieldList(): List<String> {
        return _fieldList
    }

    private val _rowParser = object : RowParser<T> {
        override fun parseRow(columns: Array<Any?>): T {
            return parseRows()(columns)
        }
    }

    fun getRowParser(): RowParser<T> {
        return _rowParser
    }
}

class ConstructionSiteMeta : Meta<ConstructionSite>() {
    override fun getTableName(): String {
        return "ConstructionSite"
    }

    override fun getColumns(): Array<Pair<String, SqlType>> {
        return arrayOf(
            ConstructionSite::id.name to TEXT + PRIMARY_KEY + UNIQUE,
            ConstructionSite::name.name to TEXT,
            ConstructionSite::streetAddress.name to TEXT,
            ConstructionSite::postalCode.name to TEXT,
            ConstructionSite::locality.name to TEXT,
            ConstructionSite::country.name to TEXT,
            ConstructionSite::imagePath.name to TEXT,
            ConstructionSite::lastChangeTime.name to TEXT
        )
    }

    override fun parseRows(): (Array<Any?>) -> ConstructionSite {
        return { columns: Array<Any?> ->
            ConstructionSite(
                columns[0] as String,
                columns[1] as String,
                columns[2] as String?,
                columns[3] as String?,
                columns[4] as String?,
                columns[5] as String?,
                columns[6] as String?,
                columns[7] as String
            )
        }
    }

    override fun toArray(element: ConstructionSite): Array<Any?> {
        return arrayOf(
            element.id,
            element.name,
            element.streetAddress,
            element.postalCode,
            element.locality,
            element.country,
            element.imagePath,
            element.lastChangeTime
        )
    }
}

class CraftsmanMeta : Meta<Craftsman>() {
    override fun getTableName(): String {
        return "Craftsman"
    }

    override fun getColumns(): Array<Pair<String, SqlType>> {
        return arrayOf(
            Craftsman::id.name to TEXT + PRIMARY_KEY + UNIQUE,
            Craftsman::constructionSiteId.name to TEXT,
            Craftsman::name.name to TEXT,
            Craftsman::trade.name to TEXT,
            Craftsman::lastChangeTime.name to TEXT
        )
    }

    override fun parseRows(): (Array<Any?>) -> Craftsman {
        return { columns: Array<Any?> ->
            Craftsman(
                columns[0] as String,
                columns[1] as String,
                columns[2] as String,
                columns[3] as String,
                columns[4] as String
            )
        }
    }

    override fun toArray(element: Craftsman): Array<Any?> {
        return arrayOf(element.id, element.constructionSiteId, element.name, element.trade, element.lastChangeTime)
    }
}

class MapMeta : Meta<Map>() {
    override fun getTableName(): String {
        return "Map"
    }

    override fun getColumns(): Array<Pair<String, SqlType>> {
        return arrayOf(
            Map::id.name to TEXT + PRIMARY_KEY + UNIQUE,
            Map::constructionSiteId.name to TEXT,
            Map::parentId.name to TEXT,
            Map::name.name to TEXT,
            Map::filePath.name to TEXT,
            Map::lastChangeTime.name to TEXT
        )
    }

    override fun parseRows(): (Array<Any?>) -> Map {
        return { columns: Array<Any?> ->
            Map(
                columns[0] as String,
                columns[1] as String,
                columns[2] as String?,
                columns[3] as String,
                columns[4] as String?,
                columns[5] as String
            )
        }
    }

    override fun toArray(element: Map): Array<Any?> {
        return arrayOf(
            element.id,
            element.constructionSiteId,
            element.parentId,
            element.name,
            element.filePath,
            element.lastChangeTime
        )
    }
}

class IssueMeta : Meta<Issue>() {
    override fun getTableName(): String {
        return "Issue"
    }

    override fun getColumns(): Array<Pair<String, SqlType>> {
        return arrayOf(
            Issue::id.name to TEXT + PRIMARY_KEY + UNIQUE,
            Issue::mapId.name to TEXT,
            Issue::wasAddedWithClient.name to TEXT,
            Issue::number.name to TEXT,
            Issue::isMarked.name to TEXT,
            Issue::imagePath.name to TEXT,
            Issue::description.name to TEXT,
            Issue::craftsmanId.name to TEXT,
            Issue::registrationTime.name to TEXT,
            Issue::registrationAuthor.name to TEXT,
            Issue::responseTime.name to TEXT,
            Issue::responseAuthor.name to TEXT,
            Issue::reviewTime.name to TEXT,
            Issue::reviewAuthor.name to TEXT,
            Issue::positionX.name to TEXT,
            Issue::positionY.name to TEXT,
            Issue::zoomScale.name to TEXT,
            Issue::mapFileID.name to TEXT,
            Issue::lastChangeTime.name to TEXT
        )
    }

    override fun parseRows(): (Array<Any?>) -> Issue {
        return { columns: Array<Any?> ->
            Issue(
                columns[0] as String,
                columns[1] as String,
                (columns[2] as String) != "0",
                (columns[3] as String?)?.toIntOrNull(),
                (columns[4] as String) != "0",
                columns[5] as String?,
                columns[6] as String?,
                columns[7] as String?,
                columns[8] as String?,
                columns[9] as String?,
                columns[10] as String?,
                columns[11] as String?,
                columns[12] as String?,
                columns[13] as String?,
                (columns[14] as String?)?.toDoubleOrNull(),
                (columns[15] as String?)?.toDoubleOrNull(),
                (columns[16] as String?)?.toDoubleOrNull(),
                columns[17] as String?,
                columns[18] as String
            )
        }
    }

    override fun toArray(element: Issue): Array<Any?> {
        return arrayOf(
            element.id,
            element.mapId,
            element.wasAddedWithClient,
            element.number,
            element.isMarked,
            element.imagePath,
            element.description,
            element.craftsmanId,
            element.registrationTime,
            element.registrationAuthor,
            element.responseTime,
            element.responseAuthor,
            element.reviewTime,
            element.reviewAuthor,
            element.positionX,
            element.positionY,
            element.zoomScale,
            element.mapFileID,
            element.lastChangeTime
        )
    }
}