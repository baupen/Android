package io.mangel.issuemanager.activities.navigation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.mangel.issuemanager.R
import io.mangel.issuemanager.models.Map
import kotlinx.android.synthetic.main.row_navigation_map.view.*

class NavigationAdapter(
    private val backClickConsumer: NavigationTreeViewModel,
    private val context: Context,
    private val currentMaps: List<Map>,
    private val mapIssueCounts: List<Int>,
    private val mapInvestigationCounts: List<Int>
) : RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val map = v.tag as Map
            backClickConsumer.mapHasBeenClicked(map.id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_navigation_map, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val map = currentMaps[position]
        holder.mapName.text = map.name
        holder.issuesCount.text = context.getString(R.string.open_issues).format(mapIssueCounts[position])

        with(holder.investigationCount) {
            if (mapInvestigationCounts[position] != 0) {
                this.text = mapInvestigationCounts[position].toString()
            } else {
                this.visibility = View.GONE
            }
        }

        with(holder.itemView) {
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