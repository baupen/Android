package io.mangel.issuemanager.activities.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.mangel.issuemanager.R

class NavigationActivity : AppCompatActivity() {
    companion object {
        const val ARGUMENTS_CONSTRUCTION_SITE_ID = "construction_site_id"
        const val ARGUMENTS_PARENT_MAP_ID = "parent_map_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        intent.extras
    }
}
