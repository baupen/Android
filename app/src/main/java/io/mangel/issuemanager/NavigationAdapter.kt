package io.mangel.issuemanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.mangel.issuemanager.models.Map
import kotlinx.android.synthetic.main.map_list_content.view.*

class NavigationAdapter(
    private val parentActivity: Navigation,
    private val currentMaps: List<Map>,
    private val mapIssueCounts: List<Int>,
    private val mapInvestigationCounts: List<Int>,
    private val firstSpecial: Boolean
) : RecyclerView.Adapter<NavigationAdapter.ViewHolder>(){

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val map = v.tag as Map
            parentActivity.mapHasBeenClicked(map.id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.map_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0){
            println("asdf")
        }
        val map = currentMaps[position]
        holder.mapName.text = map.name // TODO: null safety
        holder.issuesCount.text = "${mapIssueCounts[position]} offene Pendenzen"
        holder.investigationCount.text = mapInvestigationCounts[position].toString()
        if (mapInvestigationCounts[position] == 0) holder.investigationCount.visibility = View.GONE
        else holder.investigationCount.visibility = View.VISIBLE

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