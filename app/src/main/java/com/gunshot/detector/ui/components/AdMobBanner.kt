package com.gunshot.detector.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdMobBanner() {
    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdUnitId("ca-app-pub-3940256099942544/6300978111")
                setAdSize(AdSize.BANNER)
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}