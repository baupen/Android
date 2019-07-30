package io.mangel.issuemanager.activities.overview

import android.os.Bundle
import io.mangel.issuemanager.R
import io.mangel.issuemanager.activities.AbstractActivity
import io.mangel.issuemanager.activities.navigation.NavigationActivity
import io.mangel.issuemanager.models.ConstructionSite
import io.mangel.issuemanager.models.User
import io.mangel.issuemanager.repositories.ConstructionSitesLoaded
import kotlinx.android.synthetic.main.activity_overview.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.startActivity

class OverviewActivity : AbstractActivity(), OverviewViewModel.Overview {
    override fun setAbnahmeModus(value: Boolean) {
        getApplicationFactory().userRepository
    }

    override fun navigate(constructionSite: ConstructionSite) {
        startActivity<NavigationActivity>(NavigationActivity.ARGUMENTS_CONSTRUCTION_SITE_ID to constructionSite.id)
    }

    private var overviewViewModel: OverviewViewModel<OverviewActivity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        val user = getApplicationFactory().userRepository.getLoggedInUser()
        if (user === null) {
            finish()
        } else {
            initializeView(user)
        }

        val constructionSites = getApplicationFactory().constructionSiteRepository.getConstructionSites()

        val payload = OverviewViewModel.Payload(getApplicationFactory(), constructionSites)
        overviewViewModel = OverviewViewModel(this, contentView!!, payload)
    }

    @Suppress("unused")
    fun onConstructionSitesLoaded(event: ConstructionSitesLoaded) {
        overviewViewModel?.refreshConstructionSites()
        getApplicationFactory().syncRepository.refresh()
    }

    private fun initializeView(user: User) {
        welcome.text = getString(R.string.welcome, user.givenName);
    }
}
