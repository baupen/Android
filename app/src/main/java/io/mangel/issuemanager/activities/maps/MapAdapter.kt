package io.mangel.issuemanager.activities.maps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.mangel.issuemanager.models.Map
import kotlinx.android.synthetic.main.map_list_content.view.*

class MapAdapter(
    private val parentActivity: MapListActivity,
    private val currentMaps: List<Map>,
    private val mapIssueCounts: List<Int>,
    private val mapInvestigationCounts: List<Int>
) : RecyclerView.Adapter<MapAdapter.ViewHolder>() {

    private val onClickListener : View.OnClickListener

    // if you want to add the iOS way of making subheaders:
    // https://newfivefour.com/android-recyclerview-section-headers-view-types.html

    init {
        onClickListener = View.OnClickListener { v ->
            val map = v.tag as Map
            parentActivity.mapHasBeenClicked(map.id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(io.mangel.issuemanager.R.layout.map_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val map = currentMaps[position]
        holder.mapName.text = map.name // TODO: null safety
        holder.issuesCount.text = "${mapIssueCounts[position]} offene Pendenzen"
        holder.investigationCount.text = mapInvestigationCounts[position].toString()
        if (mapInvestigationCounts[position] == 0) holder.investigationCount.visibility = View.GONE

        with(holder.itemView){
            tag = map
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = currentMaps.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mapName: TextView = view.id_text
        val issuesCount: TextView = view.recursive_issue_count
        val investigationCount: TextView = view.to_investigate_count
    }
}