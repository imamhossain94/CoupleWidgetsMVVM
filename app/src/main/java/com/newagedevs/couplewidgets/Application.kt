package com.newagedevs.couplewidgets

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.appopen.AppOpenAd
import com.newagedevs.couplewidgets.di.persistenceModule
import com.newagedevs.couplewidgets.di.repositoryModule
import com.newagedevs.couplewidgets.di.viewModelModule
import com.newagedevs.couplewidgets.persistence.SharedPref
import com.newagedevs.couplewidgets.repository.MainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


class Application : Application() {

    private lateinit var appOpenManager: AppOpenManager
    private val preferences: SharedPref by inject()
    private val mainRepository: MainRepository by inject()

    // Background scope for non-UI tasks
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile
    private var isMobileAdsInitialized = false

    override fun onCreate() {
        super.onCreate()

        // 1. DI must be first
        setupDependencyInjection()

        // 2. Non-blocking background tasks
        applicationScope.launch {
            setupLogging()
            configureWebView()
            // Seed a default widget on first launch so the preview and the
            // Memories screen have data to show immediately.
            runCatching { mainRepository.ensureDefaultWidget() }
                .onFailure { Timber.e(it, "Failed to seed default widget") }
        }

        // AdMob is initialized only after user consent is gathered (see ConsentManager),
        // triggered from MainActivity — required for the UMP/TCF CMP policy requirement.
    }

    private fun setupDependencyInjection() {
        startKoin {
            androidContext(this@Application)
            modules(repositoryModule)
            modules(viewModelModule)
            modules(persistenceModule)
        }
    }

    private fun setupLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun configureWebView() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && packageName != getProcessName()) {
                WebView.setDataDirectorySuffix(getProcessName() ?: "default")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to configure WebView")
        }
    }

    /**
     * Initializes the AdMob SDK. Must only be called once consent has been gathered
     * (see [com.newagedevs.couplewidgets.utils.ConsentManager]) — safe to call more than
     * once, only the first call takes effect.
     */
    fun initializeMobileAdsSdk() {
        if (isMobileAdsInitialized) return
        isMobileAdsInitialized = true

        try {
            // Use the MD5 hash from logcat: "Use RequestConfiguration.Builder()..."
            val testDeviceIds = if (BuildConfig.DEBUG) {
                listOf("ED46C656ACA9172D2418F841A2B6A889")
            } else {
                emptyList()
            }
            val requestConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceIds)
                .build()
            MobileAds.setRequestConfiguration(requestConfig)

            // Initialize SDK — callback fires on the main thread
            MobileAds.initialize(this) {
                Timber.d("AdMob SDK initialized")
                appOpenManager = AppOpenManager(this)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize AdMob SDK")
        }
    }


    /**
     * Manages AdMob App Open Ads following Google's recommended pattern:
     * https://developers.google.com/admob/android/app-open
     *
     * Tracks the current foreground Activity via [ActivityLifecycleCallbacks] (required by
     * AdMob's [AppOpenAd.show]) and listens for app-foreground events via
     * [ProcessLifecycleOwner] to trigger the ad at the right moment.
     */
    inner class AppOpenManager(private val context: Context) {

        private var appOpenAd: AppOpenAd? = null
        // Use Google's demo ad unit IDs in DEBUG so test ads always load
        // (real unit IDs are rejected until AdMob account is approved)
        private val adUnitId = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/9257395921" // Demo App Open
        } else {
            BuildConfig.AD_UNIT_APP_OPEN
        }
        private var isLoadingAd = false
        private var isShowingAd = false
        private var currentActivity: Activity? = null

        // Track which Activity is currently in the foreground
        private val activityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {
                currentActivity = activity
            }
            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity
            }
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                if (currentActivity == activity) currentActivity = null
            }
        }

        // Show ad when the app comes to the foreground
        private val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                if (shouldShowAd()) showAdIfReady()
            }
        }

        init {
            registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
            ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
            loadAd()
        }

        private fun shouldShowAd(): Boolean {
            // Skip on the very first cold launch
            if (preferences.isFirstLaunch()) {
                preferences.setFirstLaunchCompleted()
                return false
            }
            return preferences.shouldShowAppOpenAd() && !isShowingAd && appOpenAd != null
        }

        private fun loadAd() {
            if (isLoadingAd || appOpenAd != null) return
            isLoadingAd = true

            AppOpenAd.load(
                context,
                adUnitId,
                AdRequest.Builder().build(),
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        Timber.d("App open ad loaded")
                        appOpenAd = ad
                        isLoadingAd = false
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Timber.w("App open ad failed to load: ${error.message}")
                        isLoadingAd = false
                        // Retry on the main thread — AdMob requires AppOpenAd.load() on main thread
                        applicationScope.launch(Dispatchers.Main) {
                            delay(30_000L)
                            loadAd()
                        }
                    }
                }
            )
        }

        private fun showAdIfReady() {
            val activity = currentActivity ?: return
            val ad = appOpenAd ?: return
            if (isShowingAd) return

            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                    Timber.d("App open ad showed")
                }

                override fun onAdDismissedFullScreenContent() {
                    isShowingAd = false
                    appOpenAd = null
                    preferences.recordAppOpenAdShown()
                    loadAd() // Pre-load the next ad
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Timber.w("App open ad failed to show: ${adError.message}")
                    isShowingAd = false
                    appOpenAd = null
                    loadAd()
                }
            }

            ad.show(activity)
        }

        fun destroy() {
            try {
                unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
                ProcessLifecycleOwner.get().lifecycle.removeObserver(lifecycleObserver)
                appOpenAd = null
            } catch (e: Exception) {
                Timber.e(e, "Error destroying AppOpenManager")
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        if (::appOpenManager.isInitialized) {
            appOpenManager.destroy()
        }
    }
}
