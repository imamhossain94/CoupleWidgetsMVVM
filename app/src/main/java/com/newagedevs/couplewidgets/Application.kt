package com.newagedevs.couplewidgets

import android.app.Application
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAppOpenAd
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.newagedevs.couplewidgets.di.persistenceModule
import com.newagedevs.couplewidgets.di.repositoryModule
import com.newagedevs.couplewidgets.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import android.os.Build
import android.webkit.WebView
import androidx.lifecycle.LifecycleEventObserver
import com.newagedevs.couplewidgets.persistence.SharedPref
import org.koin.android.ext.android.inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


class Application : Application() {

  private lateinit var appOpenManager: AppOpenManager
  private val preferences: SharedPref by inject()
  
  // Use application scope for background tasks
  private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  override fun onCreate() {
    super.onCreate()

    // Critical initialization first
    setupDependencyInjection()

    // Non-blocking initialization
    initializeInBackground()
  }

  private fun setupDependencyInjection() {
    startKoin {
      androidContext(this@Application)
      modules(repositoryModule)
      modules(viewModelModule)
      modules(persistenceModule)
    }
  }

  private fun initializeInBackground() {
    applicationScope.launch {
      // Setup logging
      setupLogging()

      // Configure WebView
      configureWebView()

      initializeAppLovinSdk()
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
      // Handle gracefully if WebView configuration fails
      Timber.e(e, "Failed to configure WebView")
    }
  }

  private fun initializeAppLovinSdk() {
    try {
      val appLovinSdk = AppLovinSdk.getInstance(this@Application)

      val initConfig = AppLovinSdkInitializationConfiguration.builder(BuildConfig.applovinSdkKey)
        .setMediationProvider(AppLovinMediationProvider.MAX)
        .apply {
          // Only add test device IDs in debug builds
          if (BuildConfig.DEBUG) {
            testDeviceAdvertisingIds = listOf("14747b03-bb4a-45c1-8654-d32632fa4812")
          }
        }
        .build()

      appLovinSdk.initialize(initConfig) {
        // Initialize app open manager after SDK is ready
        appOpenManager = AppOpenManager(this@Application)
      }

    } catch (e: Exception) {
      Timber.e(e, "Failed to initialize AppLovin SDK")
    }
  }


  inner class AppOpenManager(private val context: Context) : MaxAdListener {

    private var appOpenAd: MaxAppOpenAd? = null
    private val adUnitId = BuildConfig.appOpen_AdUnit
    private var isLoadingAd = false
    private var isShowingAd = false

    private val lifecycleObserver = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_START) {
        // Check conditions before showing ad
        if (shouldShowAd()) {
          showAdIfReady()
        }
      }
    }

    init {
      ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
      loadAd()
    }

    private fun shouldShowAd(): Boolean {
      if (preferences.isFirstLaunch()) {
        preferences.setFirstLaunchCompleted()
        return false
      }

      val shouldShow = preferences.shouldShowAd()
      val adReady = appOpenAd?.isReady == true

      return shouldShow && !isShowingAd && adReady
    }


    private fun loadAd() {
      if (isLoadingAd || appOpenAd?.isReady == true) {
        return
      }

      isLoadingAd = true

      if (appOpenAd == null) {
        appOpenAd = MaxAppOpenAd(adUnitId).apply {
          setListener(this@AppOpenManager)
        }
      }

      appOpenAd?.loadAd()
    }

    private fun showAdIfReady() {
      if (appOpenAd?.isReady == true && !isShowingAd) {
        isShowingAd = true
        appOpenAd?.showAd(adUnitId)
        preferences.saveLastAdShownTime()
      }
    }

    override fun onAdLoaded(ad: MaxAd) {
      isLoadingAd = false
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
      isLoadingAd = false

      // Retry loading after a delay
      applicationScope.launch {
        delay(30000) // Retry after 30 seconds
        loadAd()
      }
    }

    override fun onAdDisplayed(ad: MaxAd) {
      isShowingAd = true
    }

    override fun onAdClicked(ad: MaxAd) {}

    override fun onAdHidden(ad: MaxAd) {
      isShowingAd = false
      loadAd()
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
      isShowingAd = false
      loadAd()
    }

    fun destroy() {
      try {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(lifecycleObserver)
        appOpenAd?.destroy()
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


