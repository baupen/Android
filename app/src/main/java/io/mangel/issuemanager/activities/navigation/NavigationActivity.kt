package io.mangel.issuemanager.activities.navigation

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import io.mangel.issuemanager.R
import io.mangel.issuemanager.activities.AbstractActivity
import kotlinx.android.synthetic.main.activity_navigation.*


class NavigationActivity : AbstractActivity() {

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

    companion object {
        const val ROOT_ID = "root"
        const val TITLE = "title"
    }
}
