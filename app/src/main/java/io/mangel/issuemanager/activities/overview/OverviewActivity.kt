package io.mangel.issuemanager.activities.overview

import android.os.Bundle
import io.mangel.issuemanager.R
import io.mangel.issuemanager.activities.AbstractActivity
import io.mangel.issuemanager.models.User
import kotlinx.android.synthetic.main.activity_overview.*

class OverviewActivity : AbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        val user = getApplicationFactory().userRepository.getLoggedInUser()
        if (user === null) {
            finish()
        } else {
            initializeView(user)
        }
    }

    private fun initializeView(user: User) {
        welcome.text = getString(R.string.welcome, user.givenName);
    }
}
