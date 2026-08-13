package com.newagedevs.couplewidgets.persistence

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPref(val context: Context) {

    private val prefix = "cw"
    private val sharedPrefName = "${prefix}.couple_widgets_pref"

    val sharedPref: SharedPreferences = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)

    private val firstLaunchKey = "${prefix}.firstLaunch"

    // App-open ads are the most interruptive format (unrequested, full-screen on foreground),
    // so they get a much longer cooldown than interstitials, tracked independently — showing
    // one no longer resets eligibility for the other.
    private val appOpenAdCooldownMillis = 45 * 60 * 1000  // 45 minutes
    private val interstitialAdCooldownMillis = 10 * 60 * 1000  // 10 minutes
    private val lastAppOpenAdShownTimeKey = "${prefix}.lastAppOpenAdShownTime"
    private val lastInterstitialAdShownTimeKey = "${prefix}.lastInterstitialAdShownTime"
    private val interstitialClicksKey = "${prefix}.interstitialClicks"
    private val interstitialClickThreshold = 5

    fun isFirstLaunch(): Boolean {
        return sharedPref.getBoolean(firstLaunchKey, true)
    }

    fun setFirstLaunchCompleted() {
        sharedPref.edit {
            putBoolean(firstLaunchKey, false)
        }
    }

    fun recordAppOpenAdShown() {
        sharedPref.edit {
            putLong(lastAppOpenAdShownTimeKey, System.currentTimeMillis())
        }
    }

    fun recordInterstitialAdShown() {
        sharedPref.edit {
            putLong(lastInterstitialAdShownTimeKey, System.currentTimeMillis())
        }
    }

    fun shouldShowAppOpenAd(): Boolean {
        val lastShown = sharedPref.getLong(lastAppOpenAdShownTimeKey, -1L)
        val currentTime = System.currentTimeMillis()
        return lastShown == -1L || (currentTime - lastShown) > appOpenAdCooldownMillis
    }

    fun shouldShowInterstitialAds(): Boolean {
        val lastShown = sharedPref.getLong(lastInterstitialAdShownTimeKey, -1L)
        val currentTime = System.currentTimeMillis()
        val shouldShowBasedOnCooldown = lastShown == -1L || (currentTime - lastShown) > interstitialAdCooldownMillis

        // First check if enough clicks have accumulated
        val clicks = sharedPref.getInt(interstitialClicksKey, 0)
        val newClicks = clicks + 1
        val shouldShowBasedOnClicks = newClicks >= interstitialClickThreshold

        return if (shouldShowBasedOnClicks && shouldShowBasedOnCooldown) {
            // Reset click counter
            sharedPref.edit {
                putInt(interstitialClicksKey, 0)
            }
            true
        } else {
            // Increment click counter
            sharedPref.edit {
                putInt(interstitialClicksKey, newClicks)
            }
            false
        }
    }
}
