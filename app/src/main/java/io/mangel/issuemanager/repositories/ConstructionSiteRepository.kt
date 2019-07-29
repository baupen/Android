package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.api.ReadRequest
import io.mangel.issuemanager.events.AuthenticationSuccessfulEvent
import io.mangel.issuemanager.models.ConstructionSite
import io.mangel.issuemanager.models.ModelConverter
import io.mangel.issuemanager.repositories.base.AuthenticatedRepository
import io.mangel.issuemanager.services.DomainService
import io.mangel.issuemanager.services.RestHttpService
import io.mangel.issuemanager.services.data.ConstructionSiteDataService
import io.mangel.issuemanager.services.data.SqliteService
import io.mangel.issuemanager.store.AuthenticationToken
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ConstructionSiteRepository(
    private val dataService: ConstructionSiteDataService,
    private val modelConverter: ModelConverter,
    private val httpService: RestHttpService,
    private val domainService: DomainService
) : AuthenticatedRepository() {
    private val _constructionSites = ArrayList<ConstructionSite>()
    private val _constructionSiteById = HashMap<String, ConstructionSite>()

    private var isLoading = false
    private var loadingFinished = false

    fun getConstructionSites(): List<ConstructionSite> {
        synchronized(this) {
            if (!loadingFinished && !isLoading) {
                isLoading = true
                loadConstructionSites()
            }
        }

        return _constructionSites
    }

    private fun loadConstructionSites() {
        val constructionSites = dataService.getConstructionSites()
        for (constructionSite in constructionSites) {
            val model = modelConverter.convert(constructionSite)

            _constructionSiteById[constructionSite.id] = model
            _constructionSites.add(model)
        }
    }
}