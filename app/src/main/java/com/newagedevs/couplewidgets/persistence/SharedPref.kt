package com.newagedevs.couplewidgets.persistence

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPref(val context: Context) {

    private val prefix = "cw"
    private val sharedPrefName = "${prefix}.couple_widgets_pref"

    val sharedPref: SharedPreferences = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)

    private val firstLaunchKey = "${prefix}.firstLaunch"
    private val adCooldownMillis = 10 * 60 * 1000  // 10 minutes
    private val lastAdShownTimeKey = "${prefix}.LastAdShownTime"
    private val interstitialClicksKey = "${prefix}.interstitialClicks"

    fun isFirstLaunch(): Boolean {
        return sharedPref.getBoolean(firstLaunchKey, true)
    }

    fun setFirstLaunchCompleted() {
        sharedPref.edit {
            putBoolean(firstLaunchKey, false)
        }
    }

    fun saveLastAdShownTime() {
        sharedPref.edit {
            putLong(lastAdShownTimeKey, System.currentTimeMillis())
        }
    }

    private fun getLastAdShownTime(): Long {
        return sharedPref.getLong(lastAdShownTimeKey, -1L)  // Default to -1L instead of 0
    }

    fun shouldShowAd(): Boolean {
        val lastAdShownTime = getLastAdShownTime()
        val currentTime = System.currentTimeMillis()

        // Ensure that an ad has been shown before checking the cooldown
        return lastAdShownTime == -1L || (currentTime - lastAdShownTime) > adCooldownMillis
    }

    fun shouldShowInterstitialAds(): Boolean {
        // First check if enough clicks have accumulated
        val clicks = sharedPref.getInt(interstitialClicksKey, 0)
        val newClicks = clicks + 1

        // Check if we've reached the click threshold
        val shouldShowBasedOnClicks = newClicks >= 3

        // Also check if cooldown period has passed
        val shouldShowBasedOnCooldown = shouldShowAd()

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

    /**
     * Record that an ad was shown and update the cooldown timer.
     * Call this after successfully showing any ad (interstitial or app open).
     */
    fun recordAdShown() {
        saveLastAdShownTime()
    }
}
