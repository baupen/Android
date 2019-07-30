package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.api.FileDownloadRequest
import io.mangel.issuemanager.api.ObjectMeta
import io.mangel.issuemanager.api.ReadRequest
import io.mangel.issuemanager.api.tasks.*
import io.mangel.issuemanager.events.*
import io.mangel.issuemanager.factories.ClientFactory
import io.mangel.issuemanager.models.ModelConverter
import io.mangel.issuemanager.services.AuthenticationService
import io.mangel.issuemanager.services.FileService
import io.mangel.issuemanager.services.SettingService
import io.mangel.issuemanager.services.data.ConstructionSiteDataService
import io.mangel.issuemanager.services.data.CraftsmanDataService
import io.mangel.issuemanager.services.data.IssueDataService
import io.mangel.issuemanager.services.data.MapDataService
import io.mangel.issuemanager.store.StoreConverter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SyncRepository(
    private val storeConverter: StoreConverter,
    private val modelConverter: ModelConverter,
    private val constructionSiteDataService: ConstructionSiteDataService,
    private val mapDataService: MapDataService,
    private val issueDataService: IssueDataService,
    private val craftsmanDataService: CraftsmanDataService,
    private val fileService: FileService,
    private val settingService: SettingService,
    private val clientFactory: ClientFactory,
    private val authenticationService: AuthenticationService
) {
    init {
        EventBus.getDefault().register(this)
    }

    private var refreshTasksActive = 0

    fun refresh() {
        synchronized(this) {
            if (refreshTasksActive > 0) {
                return
            }

            refreshTasksActive = 1
        }

        val authToken = authenticationService.getAuthenticationToken()
        val user = settingService.readUser() ?: return

        val userMeta = ObjectMeta(user.id, user.lastChangeTime)
        val craftsmanMetas = craftsmanDataService.getAllAsObjectMeta()
        val constructionSiteMetas = constructionSiteDataService.getAllAsObjectMeta()
        val mapMetas = mapDataService.getAllAsObjectMeta()
        val issueMetas = issueDataService.getAllAsObjectMeta()

        val readRequest = ReadRequest(
            authToken.authenticationToken,
            userMeta,
            craftsmanMetas,
            constructionSiteMetas,
            mapMetas,
            issueMetas
        )
        val client = clientFactory.getClient(authToken.host)
        val readTask = ReadTask(client)
        readTask.execute(readRequest)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(event: ReadTaskFinished) {
        if (event.changedUser != null) {
            refreshUser(event.changedUser)
        }

        processChangedAndDeletedElements(event)
        EventBus.getDefault().post(SavedConstructionSitesEvent())

        downloadFiles()

        someRefreshTaskHasFinished()
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(event: ReadTaskFailed) {
        someRefreshTaskHasFinished()
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(event: TaskFinishedEvent) {
        if (event.taskType == FileDownloadTask::class.java) {
            someRefreshTaskHasFinished()
        }
    }

    private fun someRefreshTaskHasFinished() {
        refreshTasksActive--
    }

    private fun downloadFiles() {
        val authToken = authenticationService.getAuthenticationToken()
        val fileDownloadTasks = ArrayList<FileDownloadTaskEntry>()

        val constructionSiteImages = constructionSiteDataService.getConstructionSiteImages()
        for (image in constructionSiteImages) {
            if (!fileService.exists(image.imagePath)) {
                val fileDownloadRequest =
                    FileDownloadRequest(authToken.authenticationToken, constructionSite = image.meta)
                val fileDownloadTaskEntry = FileDownloadTaskEntry(fileDownloadRequest, image.imagePath)
                fileDownloadTasks.add(fileDownloadTaskEntry)
            }
        }

        val mapFiles = mapDataService.getMapImages()
        for (mapFile in mapFiles) {
            if (!fileService.exists(mapFile.filePath)) {
                val fileDownloadRequest =
                    FileDownloadRequest(authToken.authenticationToken, map = mapFile.meta)
                val fileDownloadTaskEntry = FileDownloadTaskEntry(fileDownloadRequest, mapFile.filePath)
                fileDownloadTasks.add(fileDownloadTaskEntry)
            }
        }

        val issueImages = issueDataService.getIssueImages()
        for (issueImage in issueImages) {
            if (!fileService.exists(issueImage.imagePath)) {
                val fileDownloadRequest =
                    FileDownloadRequest(authToken.authenticationToken, issue = issueImage.meta)
                val fileDownloadTaskEntry = FileDownloadTaskEntry(fileDownloadRequest, issueImage.imagePath)
                fileDownloadTasks.add(fileDownloadTaskEntry)
            }
        }

        if (fileDownloadTasks.isEmpty()) {
            return
        }

        val client = clientFactory.getClient(authToken.host)
        if (fileDownloadTasks.size > 10) {
            // make two background tasks to parallelize
            val batch1 = fileDownloadTasks.filterIndexed { index, _ -> index % 2 == 0 }
            val batch2 = fileDownloadTasks.filterIndexed { index, _ -> index % 2 == 1 }

            refreshTasksActive += 2
            FileDownloadTask(client).execute(*batch1.toTypedArray())
            FileDownloadTask(client).execute(*batch2.toTypedArray())
        } else {
            refreshTasksActive += 1
            FileDownloadTask(client).execute(*fileDownloadTasks.toTypedArray())
        }
    }

    private fun processChangedAndDeletedElements(event: ReadTaskFinished) {
        // process construction sites
        val storeConstructionSites = event.changedConstructionSites.map { cs -> storeConverter.convert(cs) }
        constructionSiteDataService.store(storeConstructionSites)
        constructionSiteDataService.delete(event.removedConstructionSiteIDs)
        if (storeConstructionSites.isNotEmpty() || event.removedConstructionSiteIDs.isNotEmpty()) {
            EventBus.getDefault().post(SavedConstructionSitesEvent())
        }

        // process craftsman
        val storeCraftsmen = event.changedCraftsmen.map { cs -> storeConverter.convert(cs) }
        craftsmanDataService.store(storeCraftsmen)
        craftsmanDataService.delete(event.removedCraftsmanIDs)
        if (storeCraftsmen.isNotEmpty() || event.removedCraftsmanIDs.isNotEmpty()) {
            EventBus.getDefault().post(SavedCraftsmenEvent())
        }

        // process maps
        val storeMaps = event.changedMaps.map { cs -> storeConverter.convert(cs) }
        mapDataService.store(storeMaps)
        mapDataService.delete(event.removedMapIDs)
        if (storeMaps.isNotEmpty() || event.removedMapIDs.isNotEmpty()) {
            EventBus.getDefault().post(SavedMapsEvent())
        }

        // process issues
        val storeIssues = event.changedIssues.map { cs -> storeConverter.convert(cs) }
        issueDataService.store(storeIssues)
        issueDataService.delete(event.removedIssueIDs)
        if (storeIssues.isNotEmpty() || event.removedIssueIDs.isNotEmpty()) {
            EventBus.getDefault().post(SavedIssuesEvent())
        }
    }

    private fun refreshUser(apiUser: io.mangel.issuemanager.api.User) {
        val storeUser = storeConverter.convert(apiUser)
        settingService.saveUser(storeUser)

        EventBus.getDefault().post(SavedUserEvent(storeUser))
    }
}