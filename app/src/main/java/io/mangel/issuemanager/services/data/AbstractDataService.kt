package io.mangel.issuemanager.services.data

import io.mangel.issuemanager.api.ObjectMeta
import io.mangel.issuemanager.services.data.store.SqliteEntry

abstract class AbstractDataService<T : SqliteEntry>(private val sqliteService: SqliteService) {
    fun getAll(): List<T> {
        return sqliteService.getAll(classOfT)
    }

    fun getAllAsObjectMeta(): List<ObjectMeta> {
        return sqliteService.getAllAsObjectMeta(classOfT)
    }

    fun store(element: T) {
        sqliteService.store(element)
    }

    fun store(elements: List<T>) {
        sqliteService.store(elements)
    }

    fun delete(ids: List<String>) {
        sqliteService.remove(classOfT, ids)
    }

    protected abstract val classOfT: Class<T>
}