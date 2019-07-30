package io.mangel.issuemanager.activities

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class IssueManagerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this);
    }
}