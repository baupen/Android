package io.mangel.issuemanager

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import io.mangel.issuemanager.activities.AbstractActivity
import io.mangel.issuemanager.activities.maps.MapListActivity
import io.mangel.issuemanager.models.Map
import kotlinx.android.synthetic.main.map_list.*
import java.io.File


class Navigation : AbstractActivity(), HandlesBackClick {

    private var currentRootId: String? = null
    private val currentMaps = ArrayList<Map>()
    private val issueCounts = ArrayList<Int>()
    private val investigationCounts = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        val toolbar: Toolbar = findViewById(R.id.nav_toolbar)

        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = CustomActionBarDrawerToggle(
            this, toolbar, drawerLayout
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        mapHasBeenClicked(intent.getStringExtra(MapListActivity.ROOT_ID), true)
    }

    override fun backWasClicked(): Boolean {
        if (currentRootId == null) return true
        mapHasBeenClicked(getApplicationFactory().mapRepository.getMap(currentRootId)!!.parent?.id)
        return false
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.navigation, menu)
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                true
            }
            android.R.id.home -> {  // press of the back button in the toolbar
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    fun mapHasBeenClicked(id: String?, firstTime: Boolean = false) {
        if (getApplicationFactory().mapRepository.getChildren(id)!!.isEmpty()){
            val iv: ImageView = findViewById(R.id.imageView2)
            val asdf = getApplicationFactory().mapRepository.getMap(id)
            val qwer = asdf!!.filePath
            val f = File(this.filesDir, qwer!!)
            val parc = ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(parc)
            val page = pdfRenderer.openPage(0)

            val height = page.height
            val width = page.width

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            iv.setImageBitmap(bitmap)
        }
        currentRootId = id
        updateRecyclerData()
        if (firstTime) {
            map_list.adapter = NavigationAdapter(
                this, currentMaps, issueCounts, investigationCounts, currentRootId != null
            )
        } else {
            map_list.adapter?.notifyDataSetChanged()
        }
        title = getCurrentTitle()
    }

    private fun updateRecyclerData() {
        val mapRepo = getApplicationFactory().mapRepository
        val issueRepo = getApplicationFactory().issueRepository
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
        if (currentRootId == null) return intent.getStringExtra(TITLE) ?: ""
        return getApplicationFactory().mapRepository.getMap(currentRootId)?.name ?: ""
    }

    companion object {
        const val ROOT_ID = "root"
        const val TITLE = "title"
    }
}
