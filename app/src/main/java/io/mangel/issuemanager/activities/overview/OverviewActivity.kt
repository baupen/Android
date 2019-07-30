package io.mangel.issuemanager.activities.overview

import android.os.Bundle
import io.mangel.issuemanager.R
import io.mangel.issuemanager.activities.AbstractActivity
import io.mangel.issuemanager.activities.navigation.NavigationActivity
import io.mangel.issuemanager.models.ConstructionSite
import org.jetbrains.anko.contentView
import org.jetbrains.anko.startActivity

class OverviewActivity : AbstractActivity(), OverviewViewModel.Overview {
    override fun setAbnahmeModusActive(value: Boolean) {
        getApplicationFactory().issueRepository.isAbnahmeModusActive = value
    }

    override fun navigate(constructionSite: ConstructionSite) {
        startActivity<NavigationActivity>(NavigationActivity.ARGUMENTS_CONSTRUCTION_SITE_ID to constructionSite.id)
    }

    private var overviewViewModel: OverviewViewModel<OverviewActivity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        val user = getApplicationFactory().userRepository.getLoggedInUser()
        val constructionSites = getApplicationFactory().constructionSiteRepository.getConstructionSites()
        val isAbnahmeModusActive = getApplicationFactory().issueRepository.isAbnahmeModusActive
        val payload = OverviewViewModel.Payload(getApplicationFactory(), user, constructionSites, isAbnahmeModusActive)

        overviewViewModel = OverviewViewModel(this, contentView!!, payload)

        getApplicationFactory().syncRepository.refresh()
    }
}
