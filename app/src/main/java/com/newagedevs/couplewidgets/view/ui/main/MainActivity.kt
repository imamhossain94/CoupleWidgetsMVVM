package com.newagedevs.couplewidgets.view.ui.main

import android.Manifest
import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.github.dhaval2404.imagepicker.ImagePicker
import com.maxkeppeler.sheets.core.SheetStyle
import com.newagedevs.couplewidgets.BuildConfig
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.databinding.ActivityMainBinding
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.view.ui.CustomSheet
import com.skydoves.bindables.BindingActivity
import com.skydoves.bundler.bundle
import com.skydoves.bundler.intentOf
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit
import kotlin.math.pow


class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val widgetID: Long? by bundle("widgetId")
    private val appWidgetIDs: IntArray? by bundle("appWidgetIds")
    private val viewModel: MainViewModel by viewModel { parametersOf(widgetID, appWidgetIDs) }

    private var retryAttempt = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding {
            vm = viewModel
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupPreviewFrame()

        createBannerAd()
        createInterstitialAd()
    }

    // ----------------------------------------------------------------
    private fun createBannerAd() {
        val bannerId = BuildConfig.banner_AdUnit
        val adView = MaxAdView(bannerId, this).apply {
            setListener(bannerAdsListener)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.banner_height)
            )
        }
        binding.adsContainer.addView(adView)
        adView.loadAd()
    }

    private val bannerAdsListener = object : MaxAdViewAdListener {
        override fun onAdLoaded(p0: MaxAd) {
            binding.adsContainer.visibility = View.VISIBLE
        }

        override fun onAdDisplayed(p0: MaxAd) {
            binding.adsContainer.visibility = View.VISIBLE
        }

        override fun onAdHidden(p0: MaxAd) {
            binding.adsContainer.visibility = View.GONE
        }

        override fun onAdClicked(p0: MaxAd) { }

        override fun onAdLoadFailed(p0: String, p1: MaxError) {
            binding.adsContainer.visibility = View.GONE
        }

        override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
            binding.adsContainer.visibility = View.GONE
        }

        override fun onAdExpanded(p0: MaxAd) { }

        override fun onAdCollapsed(p0: MaxAd) { }
    }
    // ----------------------------------------------------------------
    private fun createInterstitialAd() {
        val interstitialId = BuildConfig.interstitial_AdUnit
        viewModel.interstitialAd = MaxInterstitialAd(interstitialId, this)
        viewModel.interstitialAd.setListener(interstitialAdsListener)
        viewModel.interstitialAd.loadAd()
    }

    private val interstitialAdsListener = object : MaxAdListener {
        override fun onAdLoaded(maxAd: MaxAd) {
            retryAttempt = 0.0
        }

        override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
            retryAttempt++
            val delayMillis = TimeUnit.SECONDS.toMillis( 2.0.pow(6.0.coerceAtMost(retryAttempt)).toLong() )
            Handler(Looper.getMainLooper()).postDelayed( { viewModel.interstitialAd.loadAd()  }, delayMillis )
        }

        override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
            viewModel.interstitialAd.loadAd()
        }

        override fun onAdDisplayed(maxAd: MaxAd) { }

        override fun onAdClicked(maxAd: MaxAd) { }

        override fun onAdHidden(maxAd: MaxAd) {
            viewModel.interstitialAd.loadAd()
        }
    }
    // ----------------------------------------------------------------

    private fun setupPreviewFrame() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            val preview = findViewById<ImageView>(R.id.bg)
            val wallpaperManager = WallpaperManager.getInstance(this)

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    3
                )
                return
            }

            val wallpaperDrawable = wallpaperManager.drawable
            preview.setImageDrawable(wallpaperDrawable)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        setupPreviewFrame()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {

                val uri: Uri = data?.data!!

                when (requestCode) {
                    1094 -> {
                        viewModel.yourImage = uri
                    }
                    1095 -> {
                        viewModel.partnerImage = uri
                    }
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        CustomSheet().show(this@MainActivity) {
            style(SheetStyle.BOTTOM_SHEET)
            title("Confirm Exit")
            content("Are you sure you want to exit? Hope you will come back again.")
            onPositive("Exit") {
                finish()
            }
        }
    }


    companion object {
        fun startActivity(
            context: Context,
            couple: Couple
        ) = context.intentOf<MainActivity> {
            putExtra("widgetId", couple.id.toInt())
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