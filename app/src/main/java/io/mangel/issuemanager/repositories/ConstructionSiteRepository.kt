package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.events.LoadedConstructionSitesEvent
import io.mangel.issuemanager.events.SavedConstructionSitesEvent
import io.mangel.issuemanager.models.ConstructionSite
import io.mangel.issuemanager.models.ModelConverter
import io.mangel.issuemanager.services.data.ConstructionSiteDataService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ConstructionSiteRepository(
    private val constructionSiteDataService: ConstructionSiteDataService,
    private val modelConverter: ModelConverter
) {
    init {
        EventBus.getDefault().register(this)
    }

    private val _constructionSites = ArrayList<ConstructionSite>()
    private val _constructionSiteById = HashMap<String, ConstructionSite>()

    private var initialized = false

    fun getConstructionSites(): List<ConstructionSite> {
        synchronized(this) {
            if (!initialized) {
                initialized = true
                loadConstructionSites()
            }
        }

        return _constructionSites
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(event: SavedConstructionSitesEvent) {
        loadConstructionSites()

        EventBus.getDefault().post(LoadedConstructionSitesEvent())
    }

    private fun loadConstructionSites() {
        _constructionSites.clear()

        val constructionSites = constructionSiteDataService.getAll()
        for (constructionSite in constructionSites) {
            val model = modelConverter.convert(constructionSite)

            _constructionSiteById[constructionSite.id] = model
            _constructionSites.add(model)
        }
    }
}