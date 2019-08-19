package io.mangel.issuemanager.activities.navigation

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import io.mangel.issuemanager.R
import io.mangel.issuemanager.activities.AbstractActivity
import kotlinx.android.synthetic.main.activity_navigation.*


class Navigation : AbstractActivity() {

    private lateinit var navigationTreeViewModel: NavigationTreeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_navigation)
        val toolbar: Toolbar = findViewById(R.id.navigation_toolbar)

        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.navigation_drawer_layout)

        navigationTreeViewModel = NavigationTreeViewModel(
            this,
            intent.getStringExtra(TITLE),
            getApplicationFactory().mapRepository,
            getApplicationFactory().issueRepository,
            navigation_maps_rec_view,
            intent.getStringExtra(ROOT_ID)
        )

        val toggle = CustomActionBarDrawerToggle(
            navigationTreeViewModel, toolbar, drawerLayout
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.navigation_drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

//    fun mapHasBeenClicked(id: String?, firstTime: Boolean = false) {
//        if (getApplicationFactory().mapRepository.getChildren(id)!!.isEmpty()){
//            val iv: ImageView = findViewById(R.id.imageView2)
//            val asdf = getApplicationFactory().mapRepository.getMap(id)
//            val qwer = asdf!!.filePath
//            val f = File(this.filesDir, qwer!!)
//            val parc = ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY)
//            val pdfRenderer = PdfRenderer(parc)
//            val page = pdfRenderer.openPage(0)
//
//            val height = page.height
//            val width = page.width
//
//            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//            iv.setImageBitmap(bitmap)
//        }
//        currentRootId = id
//        updateRecyclerData()
//        if (firstTime) {
//            map_list.adapter = NavigationAdapter(
//                this, currentMaps, issueCounts, investigationCounts, currentRootId != null
//            )
//        } else {
//            map_list.adapter?.notifyDataSetChanged()
//        }
//        title = getCurrentTitle()
//    }

    companion object {
        const val ROOT_ID = "root"
        const val TITLE = "title"
    }
}
