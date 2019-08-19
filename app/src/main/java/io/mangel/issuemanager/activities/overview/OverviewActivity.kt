package io.mangel.issuemanager.activities.overview

import android.os.Bundle
import io.mangel.issuemanager.R
import io.mangel.issuemanager.activities.AbstractActivity
import io.mangel.issuemanager.api.tasks.FileDownloadFinished
import io.mangel.issuemanager.events.LoadedConstructionSitesEvent
import io.mangel.issuemanager.events.LoadedUserEvent
import io.mangel.issuemanager.models.ConstructionSite
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.iterator
import io.mangel.issuemanager.activities.navigation.NavigationActivity
import io.mangel.issuemanager.activities.login.LoginActivity
import io.mangel.issuemanager.repositories.SyncFinishedEvent
import io.mangel.issuemanager.repositories.SyncStartedEvent
import org.jetbrains.anko.*


class OverviewActivity : AbstractActivity(), OverviewViewModel.Overview {
    private var _overviewViewModel: OverviewViewModel<OverviewActivity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        val user = getApplicationFactory().userRepository.getLoggedInUser()
        val constructionSites = getApplicationFactory().constructionSiteRepository.getConstructionSites()
        val isAbnahmeModusActive = getApplicationFactory().issueRepository.isAbnahmeModusActive
        val payload = OverviewViewModel.Payload(getApplicationFactory(), user, constructionSites, isAbnahmeModusActive)

        val overviewViewModel = OverviewViewModel(this, contentView!!, payload)
        setLoadingViewModel(overviewViewModel)
        _overviewViewModel = overviewViewModel

        getApplicationFactory().syncRepository.sync()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.overview, menu)
        return true
    }


    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val alpha = if (syncActive) {
            130
        } else {
            255
        }

        for (entry in menu.iterator()) {
            entry.isEnabled = !syncActive
            entry.icon.alpha = alpha
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                getApplicationFactory().userRepository.logout()
                startActivity(intentFor<LoginActivity>().clearTop().noHistory())
                finish()
            }
            R.id.refresh -> getApplicationFactory().syncRepository.sync()
        }

        return true
    }

    override fun setAbnahmeModusActive(value: Boolean) {
        getApplicationFactory().issueRepository.isAbnahmeModusActive = value
    }

    override fun navigate(constructionSite: ConstructionSite) {
        startActivity<NavigationActivity>(
            NavigationActivity.ROOT_ID to null,
            NavigationActivity.TITLE to constructionSite.name
        )
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(event: LoadedConstructionSitesEvent) {
        _overviewViewModel?.onConstructionSitesChanged()
    }

    private var syncActive = false
    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(event: SyncStartedEvent) {
        syncActive = true
        invalidateOptionsMenu()
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(event: SyncFinishedEvent) {
        syncActive = false
        invalidateOptionsMenu()

        if (event.result == false) {
            toast(getString(R.string.sync_failed))
        } else {
            toast(getString(R.string.sync_finished))
        }
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(event: LoadedUserEvent) {
        _overviewViewModel?.onUserChanged()
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(event: FileDownloadFinished) {
        _overviewViewModel?.onFileDownloaded(event.fileName)
    }
}
