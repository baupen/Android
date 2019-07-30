package io.mangel.issuemanager.activities.overview

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.mangel.issuemanager.R
import io.mangel.issuemanager.activities.AbstractLoadingViewModel
import io.mangel.issuemanager.factories.ApplicationFactory
import io.mangel.issuemanager.models.ConstructionSite
import io.mangel.issuemanager.models.User
import kotlinx.android.synthetic.main.activity_overview.view.*
import kotlinx.android.synthetic.main.activity_overview.view.welcome

data class OverviewViewModel<T>(private val context: T, private val view: View, private val payload: Payload) :
    AbstractLoadingViewModel()
        where T : Context, T : OverviewViewModel.Overview {

    private val viewHolder = ViewHolder(view)

    init {
        viewHolder.welcomeTextView.text = context.getString(R.string.welcome, payload.user.givenName);

        val locationAdapter =
            ConstructionSiteAdapter(payload.constructionSites, context, payload.applicationFactory.fileService)
        viewHolder.constructionSiteRecyclerView.adapter = locationAdapter

        viewHolder.abnahmemodusSwitch.isChecked = payload.isAbnahmeModusActive
        viewHolder.abnahmemodusSwitch.setOnCheckedChangeListener { _, value ->
            context.setAbnahmeModusActive(value)
        }
    }

    interface Overview {
        fun setAbnahmeModusActive(value: Boolean)

        fun navigate(constructionSite: ConstructionSite)
    }

    override fun getLoadingIndicator(): ProgressBar {
        return viewHolder.loadingProgressBar
    }

    class Payload(
        val applicationFactory: ApplicationFactory,
        val user: User,
        val constructionSites: List<ConstructionSite>,
        val isAbnahmeModusActive: Boolean
    )

    class ViewHolder(view: View) {
        val welcomeTextView: TextView = view.welcome
        val constructionSiteRecyclerView: RecyclerView = view.construction_sites
        val loadingProgressBar: ProgressBar = view.loading
        val abnahmemodusSwitch: Switch = view.abnahmemodus_switch
    }
}