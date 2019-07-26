package io.mangel.issuemanager.store

import org.jetbrains.anko.db.*


abstract class Meta<T>() {
    abstract fun getTableName(): String

    abstract fun getColumns(): Array<Pair<String, SqlType>>

    abstract fun toArray(element: T): Array<String>

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

class UserMeta : Meta<User>() {
    /**
     * for safety required:
     * field definitions
     */
    override fun getTableName(): String {
        return "Users"
    }

    override fun getColumns(): Array<Pair<String, SqlType>> {
        return arrayOf(
            User::id.name to TEXT + PRIMARY_KEY + UNIQUE,
            User::lastChangeTime.name to TEXT,
            User::givenName.name to TEXT,
            User::familyName.name to TEXT
        )
    }

    override fun parseRows(): (Array<Any?>) -> User {
        return { columns: Array<Any?> ->
            User(
                columns[0] as String,
                columns[1] as String,
                columns[2] as String,
                columns[3] as String
            )
        }
    }

    override fun toArray(element: User): Array<String> {
        return arrayOf(element.id, element.lastChangeTime, element.givenName, element.familyName)
    }
}