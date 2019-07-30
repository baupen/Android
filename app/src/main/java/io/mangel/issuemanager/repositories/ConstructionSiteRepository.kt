package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.models.ConstructionSite
import io.mangel.issuemanager.models.ModelConverter
import io.mangel.issuemanager.repositories.base.AuthenticatedRepository
import io.mangel.issuemanager.services.DomainService
import io.mangel.issuemanager.services.RestHttpService
import io.mangel.issuemanager.services.data.ConstructionSiteDataService
import org.greenrobot.eventbus.EventBus


class ConstructionSiteRepository(
    private val constructionSiteDataService: ConstructionSiteDataService,
    private val modelConverter: ModelConverter
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
        val constructionSites = constructionSiteDataService.getAll()
        for (constructionSite in constructionSites) {
            val model = modelConverter.convert(constructionSite)

            _constructionSiteById[constructionSite.id] = model
            _constructionSites.add(model)
        }

        EventBus.getDefault().post(ConstructionSitesLoaded())
    }
}

class ConstructionSitesLoaded