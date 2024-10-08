@file:Suppress("unused")

package com.newagedevs.couplewidgets

import android.app.Application
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
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

class Application : Application() {
  private lateinit var appOpenManager: AppOpenManager

  override fun onCreate() {
    super.onCreate()

    startKoin {
      androidContext(this@Application)

      //Adding Module
      modules(viewModelModule)
      modules(repositoryModule)
      modules(persistenceModule)
    }

    initializeAppLovinSdk()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }

  private fun initializeAppLovinSdk() {

    // Create the initialization configuration
    val initConfig = AppLovinSdkInitializationConfiguration.builder(BuildConfig.applovinSdkKey, this)
      .setMediationProvider(AppLovinMediationProvider.MAX)
      .build()

    AppLovinSdk.getInstance(this).initialize(initConfig) { sdkConfig ->
      appOpenManager = AppOpenManager(this@Application)
    }
  }

}

@Suppress("PrivatePropertyName", "DEPRECATION")
class AppOpenManager(context: Context) : LifecycleObserver,
  MaxAdListener {
  private val appOpenAd: MaxAppOpenAd?
  private val context: Context

  //Ads ID here
  private val ADS_UNIT = BuildConfig.appOpen_AdUnit

  init {
    ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    this.context = context
    appOpenAd = MaxAppOpenAd(ADS_UNIT, context)
    appOpenAd.setListener(this)
    appOpenAd.loadAd()
  }

  private fun showAdIfReady() {
    if (appOpenAd == null || !AppLovinSdk.getInstance(context).isInitialized) return

    if (appOpenAd.isReady) {
      appOpenAd.showAd(ADS_UNIT)
    } else {
      appOpenAd.loadAd()
    }
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  fun onStart() {
    showAdIfReady()
  }

  override fun onAdLoaded(ad: MaxAd) { }
  override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
  override fun onAdDisplayed(ad: MaxAd) { }
  override fun onAdClicked(ad: MaxAd) {}
  override fun onAdHidden(ad: MaxAd) {
    appOpenAd!!.loadAd()
  }

  override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
    appOpenAd!!.loadAd()
  }
}
