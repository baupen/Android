package io.mangel.issuemanager.services.data

import io.mangel.issuemanager.services.data.store.Craftsman

class CraftsmanDataService(sqliteService: SqliteService) : AbstractDataService<Craftsman>(sqliteService) {
    override val classOfT = Craftsman::class.java
}