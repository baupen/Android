package io.mangel.issuemanager.services.data

import io.mangel.issuemanager.store.ConstructionSite

class ConstructionSiteDataService(private val sqliteService: SqliteService) {
    fun getConstructionSites(): List<ConstructionSite> {
        return sqliteService.getAll(ConstructionSite::class.java)
    }
}