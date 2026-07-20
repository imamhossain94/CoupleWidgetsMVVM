package com.newagedevs.couplewidgets.view.ui.main

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.maxkeppeler.sheets.core.SheetStyle
import com.newagedevs.couplewidgets.BuildConfig
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.databinding.ActivityMainBinding
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.view.ui.CustomSheet
import com.skydoves.bindables.BindingActivity
import com.skydoves.bundler.bundle
import com.skydoves.bundler.intentOf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.activity.OnBackPressedCallback
import com.newagedevs.couplewidgets.view.ui.widgets.WidgetsActivity
import com.newagedevs.couplewidgets.extensions.*
import com.newagedevs.couplewidgets.utils.Constants
import androidx.core.view.GravityCompat
import com.newagedevs.couplewidgets.utils.ConsentManager
import com.newagedevs.couplewidgets.utils.InAppUpdateHelper
import com.newagedevs.couplewidgets.utils.InAppUpdateHelper.Companion.REQUEST_CODE_UPDATE
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import timber.log.Timber


class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val widgetID: Long? by bundle("widgetId")
    private val appWidgetIDs: IntArray? by bundle("appWidgetIds")
    private val appWidgetID: Int? by lazy {
        intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            .takeIf { it != AppWidgetManager.INVALID_APPWIDGET_ID }
    }
    private val viewModel: MainViewModel by viewModel { parametersOf(widgetID, appWidgetIDs, appWidgetID) }

    private var retryAttempt = 0.0
    private lateinit var inAppUpdateHelper: InAppUpdateHelper
    private lateinit var consentManager: ConsentManager
    private var adsInitialized = false

    // AdMob Banner
    private var bannerAdView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        inAppUpdateHelper = InAppUpdateHelper(this)
        inAppUpdateHelper.checkForUpdate()

        binding {
            vm = viewModel
            bg.setImageResource(viewModel.getNextBackground())
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeConsentAndAds()
        animateContentEntrance()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setTitle("Confirm Exit")
                        .setMessage("Are you sure you want to exit? Hope you will come back again.")
                        .setPositiveButton("Exit") { _, _ ->
                            finish()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        })

        setupNavigationDrawer()
    }

    /** Subtle fade + rise-in for the two main sections on first load. */
    private fun animateContentEntrance() {
        listOf(binding.previewCard, binding.settingsContainer).forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 40f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(index * 80L)
                .setDuration(350L)
                .start()
        }
    }

    // ----------------------------------------------------------------
    // Consent (UMP/TCF CMP) — must be gathered before any ad is requested
    // https://developers.google.com/admob/android/privacy
    // ----------------------------------------------------------------
    private fun initializeConsentAndAds() {
        consentManager = ConsentManager(this)

        consentManager.gatherConsent { canRequestAds ->
            if (canRequestAds) initializeAds()
        }

        // Consent obtained in a previous session lets us request ads immediately
        // while requestConsentInfoUpdate() runs in parallel.
        if (consentManager.canRequestAds) {
            initializeAds()
        }
    }

    private fun initializeAds() {
        if (adsInitialized) return
        adsInitialized = true

        (application as com.newagedevs.couplewidgets.Application).initializeMobileAdsSdk()
        createBannerAd()
        loadInterstitialAd()
    }

    private fun setupNavigationDrawer() {
        binding.menuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        val drawerAction: (action: () -> Unit) -> Unit = { action ->
            action()
            binding.drawerLayout.closeDrawers()
        }

        binding.navView.findViewById<View>(R.id.nav_widgets).setOnClickListener {
            drawerAction {
                viewModel.showInterstitialIfEligible(this) { loadInterstitialAd() }
                WidgetsActivity.startActivity(this)
            }
        }
        binding.navView.findViewById<View>(R.id.nav_share).setOnClickListener {
            drawerAction { shareTheApp(this) }
        }
        binding.navView.findViewById<View>(R.id.nav_write_us).setOnClickListener {
            drawerAction { openMailApp(this, "Writing about app", Constants.contactMail) }
        }
        binding.navView.findViewById<View>(R.id.nav_feedback).setOnClickListener {
            drawerAction { openMailApp(this, "Feedback", Constants.feedbackMail) }
        }
        binding.navView.findViewById<View>(R.id.nav_bug_reports).setOnClickListener {
            drawerAction { openMailApp(this, "Bug reports", Constants.feedbackMail) }
        }
        binding.navView.findViewById<View>(R.id.nav_privacy_policy).setOnClickListener {
            drawerAction { openWebPage(this, Constants.privacyPolicyUrl) { viewModel.toast = it } }
        }
        binding.navView.findViewById<View>(R.id.nav_privacy_options).setOnClickListener {
            drawerAction {
                if (::consentManager.isInitialized && consentManager.isPrivacyOptionsRequired) {
                    consentManager.showPrivacyOptionsForm { formError ->
                        formError?.let { viewModel.toast = it.message }
                    }
                } else {
                    viewModel.toast = "Privacy options aren't required in your region"
                }
            }
        }
        binding.navView.findViewById<View>(R.id.nav_other_apps).setOnClickListener {
            drawerAction { openAppStore(this, Constants.publisherName) { viewModel.toast = it } }
        }
        binding.navView.findViewById<View>(R.id.nav_rate_us).setOnClickListener {
            drawerAction { openAppStore(this, Constants.appStoreId) { viewModel.toast = it } }
        }
        binding.navView.findViewById<View>(R.id.nav_source_code).setOnClickListener {
            drawerAction { openWebPage(this, Constants.sourceCodeUrl) { viewModel.toast = it } }
        }
        binding.navView.findViewById<View>(R.id.nav_icons_by).setOnClickListener {
            drawerAction { viewModel.toast = "Icons by svgrepo.com" }
        }
        binding.navView.findViewById<View>(R.id.nav_version).setOnClickListener {
            drawerAction { viewModel.toast = "Version: ${getApplicationVersion()}" }
        }
        binding.navView.findViewById<View>(R.id.nav_exit).setOnClickListener {
            drawerAction { finish() }
        }

        binding.navView.findViewById<TextView>(R.id.header_version_name).text = "Version: ${getApplicationVersion()}"
    }

    override fun onResume() {
        super.onResume()
        inAppUpdateHelper.onResume()
        bannerAdView?.resume()
    }

    override fun onPause() {
        super.onPause()
        bannerAdView?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerAdView?.destroy()
        bannerAdView = null
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val newWidgetId = intent.extras?.getLong("widgetId", -1L)
            ?.takeIf { it != -1L }
        val newAppWidgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?.takeIf { it != AppWidgetManager.INVALID_APPWIDGET_ID }

        viewModel.refreshData(newWidgetId, newAppWidgetId)
    }

    // ----------------------------------------------------------------
    // Banner Ad — uses anchored adaptive banner for best fill rate
    // ----------------------------------------------------------------
    private fun createBannerAd() {
        val adView = AdView(this).apply {
            adUnitId = if (BuildConfig.DEBUG) {
                "ca-app-pub-3940256099942544/6300978111" // Demo Banner
            } else {
                BuildConfig.AD_UNIT_BANNER
            }
            // Adaptive banner: fills the screen width for optimal fill/revenue
            setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                this@MainActivity,
                getAdaptiveBannerWidth()
            ))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                binding.adsContainer.visibility = View.VISIBLE
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                Timber.w("Banner ad failed to load: ${error.message}")
                binding.adsContainer.visibility = View.GONE
            }
            override fun onAdClosed() {
                binding.adsContainer.visibility = View.GONE
            }
        }

        bannerAdView = adView
        binding.adsContainer.addView(adView)
        adView.loadAd(AdRequest.Builder().build())
    }

    /** Returns the usable width in dp for the adaptive banner. */
    private fun getAdaptiveBannerWidth(): Int {
        val displayMetrics = resources.displayMetrics
        val density = displayMetrics.density
        val adWidthPixels = displayMetrics.widthPixels.toFloat()
        return (adWidthPixels / density).toInt()
    }

    // ----------------------------------------------------------------
    // Interstitial Ad — load + show pattern following AdMob guidelines
    // ----------------------------------------------------------------
    internal fun loadInterstitialAd() {
        InterstitialAd.load(
            this,
            if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/1033173712" // Demo Interstitial
            else BuildConfig.AD_UNIT_INTERSTITIAL,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Timber.d("Interstitial ad loaded")
                    retryAttempt = 0.0
                    viewModel.interstitialAd = ad

                    // Set callback so we can reload after the ad is dismissed/failed
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            viewModel.interstitialAd = null
                            loadInterstitialAd()
                        }
                        override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                            Timber.w("Interstitial failed to show: ${adError.message}")
                            viewModel.interstitialAd = null
                            loadInterstitialAd()
                        }
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Timber.w("Interstitial failed to load: ${error.message}")
                    viewModel.interstitialAd = null
                    retryAttempt++
                    val delayMillis = TimeUnit.SECONDS.toMillis(
                        2.0.pow(6.0.coerceAtMost(retryAttempt)).toLong()
                    )
                    Handler(Looper.getMainLooper()).postDelayed({ loadInterstitialAd() }, delayMillis)
                }
            }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_UPDATE -> {
                if (resultCode != RESULT_OK) {
                    Timber.e("Update flow failed! Result code: $resultCode")
                }
            }
        }

        when (resultCode) {
            RESULT_OK -> {
                val uri: Uri = data?.data!!
                when (requestCode) {
                    1094 -> { viewModel.yourImage = uri }
                    1095 -> { viewModel.partnerImage = uri }
                }
            }
            ImagePicker.RESULT_ERROR -> {
                viewModel.toast = ImagePicker.getError(data)
            }
            else -> {
                viewModel.toast = "Task Cancelled"
            }
        }
    }


    companion object {
        fun startActivity(
            context: Context,
            couple: Couple
        ) = context.intentOf<MainActivity> {
            putExtra("widgetId", couple.id)
            context.startActivity(intent, null)
            (context as Activity).finish()
        }

        fun restartActivity(
            context: Context
        ) = context.intentOf<MainActivity> {
            context.startActivity(intent, null)
            (context as Activity).finish()
        }
    }

}