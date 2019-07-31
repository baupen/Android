package io.mangel.issuemanager.activities

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

@Suppress("unused")
class IssueManagerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)
    }
}