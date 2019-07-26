package io.mangel.issuemanager.services

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import io.mangel.issuemanager.store.Meta
import io.mangel.issuemanager.store.User
import io.mangel.issuemanager.store.UserMeta
import org.jetbrains.anko.db.*

class SqliteService(context: Context) {
    private val db: IssueManagerDatabaseContext = IssueManagerDatabaseContext(context)

    fun store(element: User) {
        val meta = db.getMeta(element.javaClass)

        val tableName = meta.getTableName()
        val fieldList = meta.getFieldList().joinToString(separator = ",")
        val valuesPlaceholders = meta.getFieldList().joinToString(separator = ",") { _ -> "?" }

        db.use {
            execSQL("INSERT OR REPLACE INTO $tableName($fieldList) VALUES ($valuesPlaceholders)", meta.toArray(element))
        }
    }

    fun <T: Any> getById(id: String, classOfT: Class<T>): T? {
        val meta = db.getMeta(classOfT)
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

    private fun <T: Any> getByRelation(id: String, relationName: String, classOfT: Class<T>): List<T> {
        val meta = db.getMeta(classOfT)
        return db.use {
            select(meta.getTableName())
                .whereArgs("$relationName={id}", "id" to id)
                .exec {
                    return@exec parseList(meta.getRowParser())
                }
        }
    }

}

class IssueManagerDatabaseContext(context: Context) : ManagedSQLiteOpenHelper(context, "Issues", null, 1) {

    private val metas = hashMapOf(
        User::class.java.name to UserMeta()
    )

    override fun onCreate(db: SQLiteDatabase) {
        for ((_, meta) in metas) {
            db.createTable(meta.getTableName(), true, *meta.getColumns())
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // no upgrade needed yet
    }

    fun <T : Any> getMeta(classOfT: Class<T>): Meta<T> {
        @Suppress("UNCHECKED_CAST")
        return metas[classOfT.name] as Meta<T>
    }
}