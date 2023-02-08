package com.newagedevs.couplewidgets.extensions

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat.startActivity
import com.newagedevs.couplewidgets.BuildConfig


fun getApplicationVersion(): String {
    val versionName: String = BuildConfig.VERSION_NAME
    //val versionCode: Int = BuildConfig.VERSION_CODE
    return "Version: $versionName"
}


fun shareTheApp(context: Context) {
    ShareCompat.IntentBuilder.from((context as Activity)).setType("text/plain")
        .setChooserTitle("Chooser title")
        .setText("http://play.google.com/store/apps/details?id=" + context.packageName)
        .startChooser()
}

fun openMailApp(context: Context, subject: String, mail: Array<String>) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, mail)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        startActivity(context, intent, null)
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(
            context,
            "There are no email app installed on your device",
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun openAppStore(context: Context, link: String) {
    try {
        startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse(link)), null)
    } catch (e: ActivityNotFoundException) {
        startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse(link)), null)
    }
}

fun openWebPage(context: Context, url: String?) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(context, browserIntent, null)
}
