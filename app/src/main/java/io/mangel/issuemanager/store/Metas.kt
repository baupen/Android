package io.mangel.issuemanager.store

import org.jetbrains.anko.db.*
import kotlin.collections.Map

class MetaProvider {
    val metas: Map<String, Meta<*>> = hashMapOf(
        ConstructionSite::class.java.name to ConstructionSiteMeta()
    )

    fun <T : Any> getMeta(classOfT: Class<T>): Meta<T> {
        @Suppress("UNCHECKED_CAST")
        return metas[classOfT.name] as Meta<T>
    }
}


abstract class Meta<T>() {
    abstract fun getTableName(): String

    abstract fun getColumns(): Array<Pair<String, SqlType>>

    abstract fun toArray(element: T): Array<String?>

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
    /**
     * for safety required:
     * field definitions
     */
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

    override fun toArray(element: ConstructionSite): Array<String?> {
        return arrayOf(element.id, element.name, element.streetAddress, element.postalCode, element.locality, element.country, element.imagePath, element.lastChangeTime)
    }
}