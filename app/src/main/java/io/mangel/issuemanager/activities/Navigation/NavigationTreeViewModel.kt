package io.mangel.issuemanager.activities.Navigation

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import io.mangel.issuemanager.models.Map
import io.mangel.issuemanager.repositories.IssueRepository
import io.mangel.issuemanager.repositories.MapRepository

class NavigationTreeViewModel(
    private val parent: Activity,
    private val constructionSiteName: String?,
    private val mapRepo: MapRepository,
    private val issueRepo: IssueRepository,
    private val mapList: RecyclerView,
    startingRootId: String? = null
) : HandlesBackClick {

    private var currentRootId: String? = startingRootId
    private val currentMaps = ArrayList<Map>()
    private val issueCounts = ArrayList<Int>()
    private val investigationCounts = ArrayList<Int>()

    init {
        mapHasBeenClicked(startingRootId, true)
    }

    override fun backWasClicked(): Boolean {
        if (currentRootId == null) return true
        mapHasBeenClicked(mapRepo.getMap(currentRootId)!!.parent?.id)
        return false
    }

    fun mapHasBeenClicked(id: String?, firstTime: Boolean = false) {
        currentRootId = id
        updateRecyclerData()
        if (firstTime) {
            mapList.adapter = NavigationAdapter(
                this,
                parent,
                currentMaps,
                issueCounts,
                investigationCounts,
                currentRootId != null
            )
        } else {
            mapList.adapter?.notifyDataSetChanged()
        }
        parent.title = getCurrentTitle()
    }

    private fun updateRecyclerData() {
        // get all the current maps
        currentMaps.clear()
        if (currentRootId != null) currentMaps.add(mapRepo.getMap(currentRootId)!!)
        currentMaps.addAll(mapRepo.getChildren(currentRootId)!!.sortedBy { map -> map.name })
        // for the new maps do the counting of total open issues
        issueCounts.clear()
        issueCounts.addAll(currentMaps.map {
                map ->  issueRepo.getOpenIssuesCount(map.id, map.id != currentRootId)
        })
        // for the new maps do the counting of total issues to investigate
        investigationCounts.clear()
        investigationCounts.addAll(currentMaps.map {
                map -> issueRepo.getToInvestigateIssuesCount(map.id, map.id != currentRootId)
        })
    }

    private fun getCurrentTitle(): String {
        if (currentRootId == null) return constructionSiteName ?: ""
        return mapRepo.getMap(currentRootId)?.name ?: ""
    }

}