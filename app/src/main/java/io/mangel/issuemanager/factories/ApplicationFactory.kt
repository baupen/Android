package io.mangel.issuemanager.factories

import android.content.Context
import io.mangel.issuemanager.repositories.UserRepository
import io.mangel.issuemanager.services.DomainService
import io.mangel.issuemanager.services.NotificationService
import io.mangel.issuemanager.services.RestHttpService

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

    public val notificationService = NotificationService(context)
    private val httpService = RestHttpService(notificationService)
    private val domainService = DomainService(httpService)

    val userRepository = UserRepository(httpService, domainService)
}