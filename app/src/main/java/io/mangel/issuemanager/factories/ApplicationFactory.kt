package io.mangel.issuemanager.factories

import android.content.Context
import io.mangel.issuemanager.models.ModelConverter
import io.mangel.issuemanager.repositories.*
import io.mangel.issuemanager.services.*
import io.mangel.issuemanager.services.SettingService
import io.mangel.issuemanager.services.data.*
import io.mangel.issuemanager.store.MetaProvider
import io.mangel.issuemanager.store.StoreConverter

public class ApplicationFactory(context: Context) {
    companion object {
        private var defaultInstance: ApplicationFactory? = null

        fun getInstance(context: Context): ApplicationFactory {
            synchronized(this) {
                if (defaultInstance == null) {
                    defaultInstance = ApplicationFactory(context)
                }

                return defaultInstance!!
            }
        }
    }

    val notificationService = NotificationService(context)

    private val storeConverter = StoreConverter()
    private val modelConverter = ModelConverter()

    val fileService = FileService(context.filesDir)

    private val serializationService = SerializationService()
    private val httpService = RestHttpService(notificationService, fileService)
    private val clientFactory = ClientFactory(httpService, serializationService)

    private val sharedPreferences = context.getSharedPreferences("io.mangel.issuemanager", Context.MODE_PRIVATE)
    private val settingService = SettingService(sharedPreferences, serializationService)
    private val domainService = DomainService()
    private val metaProvider = MetaProvider()

    private val sqliteService = SqliteService(metaProvider, context)
    private val constructionSiteDataService = ConstructionSiteDataService(sqliteService)
    private val mapDataService = MapDataService(sqliteService)
    private val issueDataService = IssueDataService(sqliteService)
    private val craftsmanDataService = CraftsmanDataService(sqliteService)


    val domainRepository = DomainOverridesRepository(clientFactory)

    val userRepository = UserRepository(
        domainRepository,
        domainService,
        storeConverter,
        modelConverter,
        settingService,
        clientFactory
    )

    val constructionSiteRepository = ConstructionSiteRepository(
        constructionSiteDataService,
        modelConverter
    )

    val mapRepository = MapRepository()

    val issueRepository = IssueRepository()

    val craftsmanRepository = CraftsmanRepository()

    val syncRepository = SyncRepository(
        storeConverter,
        modelConverter,
        constructionSiteDataService,
        mapDataService,
        issueDataService,
        craftsmanDataService,
        fileService,
        settingService,
        clientFactory
    )
}