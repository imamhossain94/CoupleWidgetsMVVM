package com.newagedevs.couplewidgets.utils

import android.app.Activity
import android.content.Context
import androidx.core.content.edit
import com.google.android.play.core.review.ReviewManagerFactory
import timber.log.Timber

/**
 * Manager class for handling in-app rating functionality
 * Uses Google Play's In-App Review API
 */
class InAppRatingManager(private val context: Context) {

    private val reviewManager = ReviewManagerFactory.create(context)
    private val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "in_app_rating_prefs"
        private const val KEY_ACTION_COUNT = "action_count"
        private const val KEY_NEVER_ASK = "never_ask"
        
        // Configuration constants
        private const val ACTIONS_UNTIL_PROMPT = 2 // Show after every 2 widgets
    }

    /**
     * Increment the action count and check if we should show the rating prompt
     */
    fun onActionCompleted(activity: Activity) {
        if (shouldNeverAsk()) {
            return
        }

        val actionCount = sharedPrefs.getInt(KEY_ACTION_COUNT, 0) + 1
        sharedPrefs.edit {
            putInt(KEY_ACTION_COUNT, actionCount)
        }

        Timber.d("Action completed, total actions: $actionCount")

        if (shouldShowPrompt(actionCount)) {
            showRatingPrompt(activity)
        }
    }

    /**
     * Manually trigger the rating prompt
     */
    fun showRatingPrompt(activity: Activity) {
        if (shouldNeverAsk()) {
            Timber.d("User has opted out of rating prompts")
            return
        }

        // Using requestReviewInfo() as it is the standard Play Core method.
        // If this still fails, double check the library version compatibility.
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener {
                    // Reset action count after showing prompt
                    sharedPrefs.edit {
                        putInt(KEY_ACTION_COUNT, 0) // Reset action count
                    }
                    Timber.d("Rating flow completed, action count reset")
                }
            } else {
                Timber.e("Failed to request review flow: ${task.exception?.message}")
            }
        }
    }

    /**
     * Check if we should show the rating prompt
     */
    private fun shouldShowPrompt(actionCount: Int): Boolean {
        // Show prompt every 5 actions
        return actionCount >= ACTIONS_UNTIL_PROMPT && actionCount % ACTIONS_UNTIL_PROMPT == 0
    }

    /**
     * Check if user has opted out of rating prompts
     */
    private fun shouldNeverAsk(): Boolean {
        return sharedPrefs.getBoolean(KEY_NEVER_ASK, false)
    }

    /**
     * Opt the user out of rating prompts permanently
     */
    fun neverAskAgain() {
        sharedPrefs.edit {
            putBoolean(KEY_NEVER_ASK, true)
        }
    }

    /**
     * Reset all rating preferences (useful for testing)
     */
    fun resetPreferences() {
        sharedPrefs.edit {
            clear()
        }
    }
}
