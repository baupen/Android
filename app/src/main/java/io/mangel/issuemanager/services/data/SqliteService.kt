package io.mangel.issuemanager.services.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import io.mangel.issuemanager.api.ObjectMeta
import io.mangel.issuemanager.services.data.store.MetaProvider
import io.mangel.issuemanager.services.data.store.SqliteEntry
import org.jetbrains.anko.db.*

class SqliteService(private val metaProvider: MetaProvider, context: Context) {
    private val db: IssueManagerDatabaseContext =
        IssueManagerDatabaseContext(metaProvider, context)

    fun clearDatabase() {
        db.use {
            for (table in metaProvider.supported) {
                val meta = metaProvider.getMeta(table)
                execSQL("DELETE FROM ${meta.getTableName()}")
            }
        }
    }

    fun <T : SqliteEntry> store(element: T) {
        val meta = metaProvider.getMeta(element.javaClass)

        val tableName = meta.getTableName()
        val fieldList = meta.getFieldList().joinToString(separator = ",")
        val valuesPlaceholders = meta.getFieldList().joinToString(separator = ",") { "?" }

        db.use {
            execSQL("INSERT OR REPLACE INTO $tableName($fieldList) VALUES ($valuesPlaceholders)", meta.toArray(element))
        }
    }

    fun <T : SqliteEntry> store(elements: List<T>) {
        if (elements.isEmpty()) {
            return
        }

        val meta = metaProvider.getMeta(elements.first().javaClass)

        val tableName = meta.getTableName()
        val fieldList = meta.getFieldList().joinToString(separator = ",")
        val valuesPlaceholders = meta.getFieldList().joinToString(separator = ",") { "?" }

        db.use {
            val sqlStatement = "INSERT OR REPLACE INTO $tableName($fieldList) VALUES ($valuesPlaceholders)"

            for (element in elements) {
                execSQL(sqlStatement, meta.toArray(element))
            }
        }
    }

    fun <T : SqliteEntry> remove(classOfT: Class<T>, elements: List<String>) {
        if (elements.isEmpty()) {
            return
        }

        val meta = metaProvider.getMeta(classOfT)
        val tableName = meta.getTableName()

        db.use {
            execSQL("DELETE FROM $tableName WHERE id IN (\"${elements.joinToString(separator = "\",\"")}\")")
        }
    }

    fun <T : SqliteEntry> getAllAsObjectMeta(classOfT: Class<T>): List<ObjectMeta> {
        val meta = metaProvider.getMeta(classOfT)
        val rowParser = MetaRowParser()
        return db.use {
            select(meta.getTableName(), SqliteEntry::id.name, SqliteEntry::lastChangeTime.name)
                .whereArgs("")
                .exec {
                    return@exec parseList(rowParser)
                }
        }
    }

    fun <T : SqliteEntry, T2 : Any> getAllWithNonNullFieldAsCustom(
        classOfT: Class<T>,
        rowParser: RowParser<T2>,
        nonNullField: String,
        vararg fields: String
    ): List<T2> {
        val meta = metaProvider.getMeta(classOfT)
        return db.use {
            select(meta.getTableName(), nonNullField, *fields)
                .whereArgs("$nonNullField IS NOT NULL")
                .exec {
                    return@exec parseList(rowParser)
                }
        }
    }

    class MetaRowParser : RowParser<ObjectMeta> {
        override fun parseRow(columns: Array<Any?>): ObjectMeta {
            return ObjectMeta(columns[0] as String, columns[1] as String)
        }
    }


    fun <T : SqliteEntry> getById(id: String, classOfT: Class<T>): T? {
        val meta = metaProvider.getMeta(classOfT)
        return db.use {
            select(meta.getTableName())
                .whereArgs("id={id}", "id" to id)
                .exec {
                    when (count) {
                        1 -> return@exec parseSingle(meta.getRowParser())
                        else -> return@exec null
                    }
                }
        }
    }

    fun <T : SqliteEntry> getAll(classOfT: Class<T>): List<T> {
        val meta = metaProvider.getMeta(classOfT)
        return db.use {
            select(meta.getTableName())
                .exec {
                    return@exec parseList(meta.getRowParser())
                }
        }
    }

    private fun <T : SqliteEntry> getByRelation(id: String, relationName: String, classOfT: Class<T>): List<T> {
        val meta = metaProvider.getMeta(classOfT)
        return db.use {
            select(meta.getTableName())
                .whereArgs("$relationName={id}", "id" to id)
                .exec {
                    return@exec parseList(meta.getRowParser())
                }
        }
    }

}

class IssueManagerDatabaseContext(private val metaProvider: MetaProvider, context: Context) :
    ManagedSQLiteOpenHelper(context, "Issues.sqlite", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        for (table in metaProvider.supported) {
            val meta = metaProvider.getMeta(table)
            db.createTable(meta.getTableName(), true, *meta.getColumns())
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // no upgrade needed yet
    }
}