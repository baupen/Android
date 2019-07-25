package io.mangel.issuemanager.factories

import android.content.Context
import android.content.res.AssetManager
import io.mangel.issuemanager.repositories.UserRepository
import io.mangel.issuemanager.services.DomainService
import io.mangel.issuemanager.services.RestHttpService

public class RepositoryFactory(context: Context) {
    companion object {
        private var defaultInstance: RepositoryFactory? = null

        fun getInstance(context: Context): RepositoryFactory {
            synchronized(this) {
                if (defaultInstance == null) {
                    defaultInstance = RepositoryFactory(context)
                }

                return defaultInstance!!
            }
        }
    }

    private val httpService = RestHttpService()
    private val domainService = DomainService(httpService)

    val userRepository = UserRepository(httpService, domainService)
}