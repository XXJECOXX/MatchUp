package com.epyco.matchup

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ActivityInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        (findViewById<View>(R.id.website) as TextView).text =
            "Website: " + getString(R.string.website)
        (findViewById<View>(R.id.privacy) as TextView).text =
            "Privacy Policy: " + getString(R.string.website) + "/privacy.pdf"
        try {
            val appInfo = this.packageManager.getPackageInfo(this.packageName, 0)
            (findViewById<View>(R.id.versionText) as TextView).text =
                getString(R.string.app_name) + " v" + appInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
        }
    }

    fun back(v: View?) {
        onBackPressed()
    }
}