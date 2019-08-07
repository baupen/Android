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
    private val maps: List<Map>,
    private val mapIdToRecursiveIssueCount: HashMap<String, Int>,
    private val mapIdToInvestigationCount: HashMap<String, Int>
) : RecyclerView.Adapter<MapAdapter.ViewHolder>() {

    private val onClickListener : View.OnClickListener

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
        val map = maps.sortedBy { m -> m.name } [position]  // TODO: make efficient
        holder.mapName.text = map.name // TODO: null safety
        holder.issuesCount.text = "${mapIdToRecursiveIssueCount[map.id]} offene Pendenzen"
        val nbrOfIssuesToInvestigate = mapIdToInvestigationCount[map.id] ?: 0
        if (nbrOfIssuesToInvestigate == 0) holder.investigationCount.visibility = View.GONE
        else holder.investigationCount.text = nbrOfIssuesToInvestigate.toString()

        with(holder.itemView){
            tag = map
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = maps.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mapName: TextView = view.id_text
        val issuesCount: TextView = view.recursive_issue_count
        val investigationCount: TextView = view.to_investigate_count
    }
}