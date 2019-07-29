package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.api.Client
import io.mangel.issuemanager.api.ObjectMeta
import io.mangel.issuemanager.api.ReadRequest
import io.mangel.issuemanager.api.tasks.ReadTask
import io.mangel.issuemanager.events.UserLoadedEvent
import io.mangel.issuemanager.models.User
import io.mangel.issuemanager.repositories.base.AuthenticatedRepository
import io.mangel.issuemanager.services.RestHttpService
import io.mangel.issuemanager.services.SerializationService
import io.mangel.issuemanager.services.SettingService
import io.mangel.issuemanager.services.data.SqliteService
import io.mangel.issuemanager.store.ConstructionSite
import io.mangel.issuemanager.store.Craftsman
import io.mangel.issuemanager.store.Issue
import io.mangel.issuemanager.store.Map
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SyncRepository(private val sqliteService: SqliteService, private val settingService: SettingService, private val httpService: RestHttpService, private val serializationService: SerializationService) : AuthenticatedRepository() {
    fun refresh() {
        val authToken = getAuthenticationToken() ?: return
        val user = settingService.readUser() ?: return

        val userMeta = ObjectMeta(user.id, user.lastChangeTime)
        val craftsmanMetas = sqliteService.getIdLastChangeTimePair(Craftsman::class.java)
        val constructionSiteMetas = sqliteService.getIdLastChangeTimePair(ConstructionSite::class.java)
        val mapMetas = sqliteService.getIdLastChangeTimePair(Map::class.java)
        val issueMetas = sqliteService.getIdLastChangeTimePair(Issue::class.java)

        val readRequest = ReadRequest(authToken.authenticationToken, userMeta, craftsmanMetas, constructionSiteMetas, mapMetas, issueMetas)
        val client = Client(authToken.host, httpService, serializationService)
        val readTask = ReadTask(client)
        readTask.execute(readRequest)
    }

    private var _user: User? = null

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserLoaded(event: UserLoadedEvent) {
        _user = event.user
    }
}