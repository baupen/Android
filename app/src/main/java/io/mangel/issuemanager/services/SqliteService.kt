package io.mangel.issuemanager.services

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import io.mangel.issuemanager.store.*
import org.jetbrains.anko.db.*

class SqliteService(private val metaProvider: MetaProvider, context: Context) {
    private val db: IssueManagerDatabaseContext = IssueManagerDatabaseContext(metaProvider, context)

    fun <T: SqliteEntry<T>> store(element: T) {
        val meta = metaProvider.getMeta(element.javaClass)

        val tableName = meta.getTableName()
        val fieldList = meta.getFieldList().joinToString(separator = ",")
        val valuesPlaceholders = meta.getFieldList().joinToString(separator = ",") { _ -> "?" }

        db.use {
            execSQL("INSERT OR REPLACE INTO $tableName($fieldList) VALUES ($valuesPlaceholders)", meta.toArray(element))
        }
    }

    fun <T: SqliteEntry<T>> getById(id: String, classOfT: Class<T>): T? {
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

    private fun <T: SqliteEntry<T>> getByRelation(id: String, relationName: String, classOfT: Class<T>): List<T> {
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

class IssueManagerDatabaseContext(private val metaProvider: MetaProvider, context: Context) : ManagedSQLiteOpenHelper(context, "Issues", null, 1) {

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