package io.mangel.issuemanager.activities.maps

import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import io.mangel.issuemanager.R
import io.mangel.issuemanager.activities.AbstractActivity
import io.mangel.issuemanager.models.Map
import kotlinx.android.synthetic.main.activity_map_list.*
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
    private fun currentRoot() = intent.getStringExtra(ROOT_ID)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_list)
        setSupportActionBar(toolbar)

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
        val issueRepo = getApplicationFactory().issueRepository
        val mapsToBeDisplayed = mapsToBeDisplayed()
        val issueCounts = mapsToBeDisplayed.map {
                map ->  issueRepo.getOpenIssuesCount(map.id,map.id != currentRoot())
        }
        val investigateCounts = mapsToBeDisplayed.map {
                map -> issueRepo.getToInvestigateIssuesCount(map.id, map.id != currentRoot())
        }
        recyclerView.adapter = MapAdapter(
            this, mapsToBeDisplayed, issueCounts, investigateCounts
        )
    }

    private fun mapsToBeDisplayed() : List<Map> {
        val currentRoot = currentRoot()
        val children = getApplicationFactory().mapRepository.getChildren(currentRoot)!!.sortedBy { map -> map.name }
        if (currentRoot == null) return children
        return listOf(getApplicationFactory().mapRepository.getMap(currentRoot)!!) + children
    }

    fun mapHasBeenClicked(mapId: String) {
        if (mapId == currentRoot() || getApplicationFactory().mapRepository.getChildren(mapId)!!.isEmpty()){
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
}
