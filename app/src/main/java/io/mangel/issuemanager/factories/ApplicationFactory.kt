package io.mangel.issuemanager.factories

import android.content.Context
import io.mangel.issuemanager.models.ModelConverter
import io.mangel.issuemanager.repositories.DomainOverridesRepository
import io.mangel.issuemanager.repositories.UserRepository
import io.mangel.issuemanager.services.*
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

    private val storeConverter = StoreConverter()
    private val modelConverter = ModelConverter()

    private val serializationService = SerializationService()
    val notificationService = NotificationService(context)
    private val sharedPreferences = context.getSharedPreferences("io.mangel.issuemanager", Context.MODE_PRIVATE)
    private val settingService = SettingService(sharedPreferences, serializationService)
    private val httpService = RestHttpService(notificationService)
    private val domainService = DomainService()
    private val metaProvider = MetaProvider()
    private val sqliteService = SqliteService(metaProvider, context)

    val domainRepository = DomainOverridesRepository(httpService, serializationService)
    val userRepository = UserRepository(
        httpService,
        domainRepository,
        domainService,
        storeConverter,
        modelConverter,
        sqliteService,
        settingService,
        serializationService
    )
}