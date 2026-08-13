package com.newagedevs.couplewidgets.utils

import android.app.Activity
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import com.newagedevs.couplewidgets.BuildConfig
import timber.log.Timber

/**
 * Wraps Google's User Messaging Platform (UMP) SDK — the certified TCF CMP required by
 * AdMob policy for serving personalized ads to users in the EEA, UK, and Switzerland.
 * https://developers.google.com/admob/android/privacy
 */
class ConsentManager(private val activity: Activity) {

    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(activity)

    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    val isPrivacyOptionsRequired: Boolean
        get() = consentInformation.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    /**
     * Requests up-to-date consent info and shows the consent form if required.
     * [onConsentGatheringComplete] may be invoked more than once is not guaranteed here —
     * callers should treat it as "safe to request ads now" and guard their own init.
     */
    fun gatherConsent(onConsentGatheringComplete: (canRequestAds: Boolean) -> Unit) {
        val paramsBuilder = ConsentRequestParameters.Builder()

        if (BuildConfig.DEBUG) {
            val debugSettings = ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .build()
            paramsBuilder.setConsentDebugSettings(debugSettings)
        }

        consentInformation.requestConsentInfoUpdate(
            activity,
            paramsBuilder.build(),
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        Timber.w("Consent form error (${formError.errorCode}): ${formError.message}")
                    }
                    onConsentGatheringComplete(consentInformation.canRequestAds())
                }
            },
            { requestConsentError ->
                Timber.w("Consent info update failed (${requestConsentError.errorCode}): ${requestConsentError.message}")
                onConsentGatheringComplete(consentInformation.canRequestAds())
            }
        )
    }

    /** Lets users in the EEA/UK/CH revisit their consent choice, e.g. from a settings menu. */
    fun showPrivacyOptionsForm(onDismissed: (FormError?) -> Unit) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onDismissed)
    }
}
