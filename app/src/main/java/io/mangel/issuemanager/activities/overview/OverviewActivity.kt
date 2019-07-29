package io.mangel.issuemanager.activities.overview

import android.os.Bundle
import io.mangel.issuemanager.R
import io.mangel.issuemanager.activities.AbstractActivity
import io.mangel.issuemanager.models.ConstructionSite
import io.mangel.issuemanager.models.User
import io.mangel.issuemanager.repositories.ConstructionSitesLoaded
import kotlinx.android.synthetic.main.activity_overview.*
import org.jetbrains.anko.contentView

class OverviewActivity : AbstractActivity(), OverviewViewModel.Overview {
    override fun setAbnahmeModus(value: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun navigate(constructionSite: ConstructionSite) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
    }

    private fun initializeView(user: User) {
        welcome.text = getString(R.string.welcome, user.givenName);
    }
}
