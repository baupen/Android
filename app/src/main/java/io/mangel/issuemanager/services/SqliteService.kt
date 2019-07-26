package io.mangel.issuemanager.services

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import io.mangel.issuemanager.store.User
import org.jetbrains.anko.db.*

class SqliteService(context: Context) {
    private val db: IssueManagerDatabaseContext = IssueManagerDatabaseContext(context)

    fun store(element: User) {
        db.use {
            insert(
                Tables.TABLE_NAME_USER,
                User::id.name to element.id,
                User::lastChangeTime.name to element.lastChangeTime,
                User::givenName.name to element.givenName,
                User::familyName.name to element.familyName
            )
        }
    }

    fun getUser(id: String) {
        return db.use {
            val rowParser = classParser<User>()
            select(Tables.TABLE_NAME_USER)
                .whereArgs("id={id}", "id" to id)
                .exec {
                    parseSingle(rowParser)
                }
        }
    }
}

class Tables {
    companion object {
        const val TABLE_NAME_USER = "Users"
    }
}

class IssueManagerDatabaseContext(context: Context) : ManagedSQLiteOpenHelper(context, "Issues", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // user table
        db.createTable(
            Tables.TABLE_NAME_USER,
            true,
            User::id.name to TEXT + PRIMARY_KEY + UNIQUE,
            User::lastChangeTime.name to TEXT,
            User::givenName.name to TEXT,
            User::familyName.name to TEXT
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // no upgrade needed yet
    }
}