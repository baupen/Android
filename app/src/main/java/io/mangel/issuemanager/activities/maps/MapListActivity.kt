package io.mangel.issuemanager.activities.maps

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.MenuItem
import io.mangel.issuemanager.R
import io.mangel.issuemanager.activities.AbstractActivity

import io.mangel.issuemanager.dummy.DummyContent
import io.mangel.issuemanager.models.Map
import kotlinx.android.synthetic.main.activity_map_list.*
import kotlinx.android.synthetic.main.map_list_content.view.*
import kotlinx.android.synthetic.main.map_list.*
import org.jetbrains.anko.startActivity

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [MapDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class MapListActivity : AbstractActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    private var currentRoot: String? = null
    private var currentMaps: List<Map> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_list)
        setSupportActionBar(toolbar)

        currentRoot = intent.getStringExtra(ROOT_ID)
        currentMaps = getApplicationFactory().mapRepository.getChildren(currentRoot)!!

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra(TITLE)

        twoPane = map_detail_container != null  // is only true for large screens
        setupRecyclerView(map_list)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {  // press of the back button in the toolbar
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                super.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = MapAdapter(
            this,
            currentMaps,
            getApplicationFactory().issueRepository.getMapIdToOpenIssueCount(),
            getApplicationFactory().issueRepository.getMapIdToInvestigationCount()
        )
    }

    fun mapHasBeenClicked(mapId: String) {
        if (mapId == currentRoot || getApplicationFactory().mapRepository.getChildren(mapId)!!.isEmpty()){
            openDetailActivity(mapId)
        } else {
            val title = getApplicationFactory().mapRepository.getMap(mapId)?.name ?: "Kein Name"
            startActivity<MapListActivity>(ROOT_ID to mapId, TITLE to title)
        }
    }

    private fun openDetailActivity(mapId: String?) {
        if (twoPane) {
            val fragment = MapDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(MapDetailFragment.ARG_ITEM_ID, mapId)
                }
            }
            this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.map_detail_container, fragment)
                .commit()
        } else {
            startActivity<MapDetailActivity>(
                MapDetailFragment.ARG_ITEM_ID to mapId
            )
        }
    }

    companion object {
        const val ROOT_ID = "root"
        const val TITLE = "title"
    }

    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: MapListActivity,
        private val values: List<DummyContent.DummyItem>,
        private val twoPane: Boolean
    ) : RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as DummyContent.DummyItem
                if (twoPane) {
                    val fragment = MapDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(MapDetailFragment.ARG_ITEM_ID, item.id)
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.map_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, MapDetailActivity::class.java).apply {
                        putExtra(MapDetailFragment.ARG_ITEM_ID, item.id)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.map_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = item.id

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.id_text
        }
    }
}
