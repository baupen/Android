package io.mangel.issuemanager.activities.overview

import android.os.Bundle
import io.mangel.issuemanager.R
import io.mangel.issuemanager.activities.AbstractActivity
import kotlinx.android.synthetic.main.activity_overview.*

class OverviewActivity : AbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        val user = applicationFactory.userRepository.getLoggedInUser()
        welcome.text = getString(R.string.welcome, user.givenName);
    }
}
