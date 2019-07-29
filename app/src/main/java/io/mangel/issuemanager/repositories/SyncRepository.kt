package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.api.Client
import io.mangel.issuemanager.api.ObjectMeta
import io.mangel.issuemanager.api.ReadRequest
import io.mangel.issuemanager.api.tasks.ReadTask
import io.mangel.issuemanager.api.tasks.ReadTaskFinished
import io.mangel.issuemanager.events.RefreshedEvent
import io.mangel.issuemanager.events.UserLoadedEvent
import io.mangel.issuemanager.events.UserRefreshedEvent
import io.mangel.issuemanager.models.ModelConverter
import io.mangel.issuemanager.models.User
import io.mangel.issuemanager.repositories.base.AuthenticatedRepository
import io.mangel.issuemanager.services.RestHttpService
import io.mangel.issuemanager.services.SerializationService
import io.mangel.issuemanager.services.SettingService
import io.mangel.issuemanager.services.data.SqliteService
import io.mangel.issuemanager.store.*
import io.mangel.issuemanager.store.Map
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SyncRepository(
    private val storeConverter: StoreConverter,
    private val modelConverter: ModelConverter,
    private val sqliteService: SqliteService,
    private val settingService: SettingService,
    private val httpService: RestHttpService,
    private val serializationService: SerializationService
) : AuthenticatedRepository() {
    fun refresh() {
        val authToken = getAuthenticationToken() ?: return
        val user = settingService.readUser() ?: return

        val userMeta = ObjectMeta(user.id, user.lastChangeTime)
        val craftsmanMetas = sqliteService.getIdLastChangeTimePair(Craftsman::class.java)
        val constructionSiteMetas = sqliteService.getIdLastChangeTimePair(ConstructionSite::class.java)
        val mapMetas = sqliteService.getIdLastChangeTimePair(Map::class.java)
        val issueMetas = sqliteService.getIdLastChangeTimePair(Issue::class.java)

        val readRequest = ReadRequest(
            authToken.authenticationToken,
            userMeta,
            craftsmanMetas,
            constructionSiteMetas,
            mapMetas,
            issueMetas
        )
        val client = Client(authToken.host, httpService, serializationService)
        val readTask = ReadTask(client)
        readTask.execute(readRequest)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReadTaskFinished(event: ReadTaskFinished) {
        if (event.changedUser != null) {
            refreshUser(event.changedUser)
        }

        // process construction sites
        val storeConstructionSites = event.changedConstructionSites.map { cs -> storeConverter.convert(cs) }
        sqliteService.store(storeConstructionSites)
        sqliteService.remove(ConstructionSite::class.java, event.removedConstructionSiteIDs.map { cs -> cs.toString() })
        
        // process craftsman
        val storeCraftsmen = event.changedCraftsmen.map { cs -> storeConverter.convert(cs) }
        sqliteService.store(storeCraftsmen)
        sqliteService.remove(Craftsman::class.java, event.removedCraftsmanIDs.map { cs -> cs.toString() })
        
        // process maps
        val storeMaps = event.changedMaps.map { cs -> storeConverter.convert(cs) }
        sqliteService.store(storeMaps)
        sqliteService.remove(Map::class.java, event.removedMapIDs.map { cs -> cs.toString() })

        // process issues
        val storeIssues = event.changedIssues.map { cs -> storeConverter.convert(cs) }
        sqliteService.store(storeIssues)
        sqliteService.remove(Issue::class.java, event.removedIssueIDs.map { cs -> cs.toString() })

        EventBus.getDefault().post(RefreshedEvent())
    }

    private fun refreshUser(apiUser: io.mangel.issuemanager.api.User) {
        val storeUser = storeConverter.convert(apiUser)
        settingService.saveUser(storeUser)

        val user = modelConverter.convert(storeUser)
        _user = user
        EventBus.getDefault().post(UserRefreshedEvent(user))
    }


    private var _user: User? = null

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserLoaded(event: UserLoadedEvent) {
        _user = event.user
    }
}