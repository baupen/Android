package io.mangel.issuemanager.services.data

import io.mangel.issuemanager.store.Craftsman

class CraftsmanDataService(private val sqliteService: SqliteService) : AbstractDataService<Craftsman>(sqliteService) {
    override val classOfT = Craftsman::class.java
}