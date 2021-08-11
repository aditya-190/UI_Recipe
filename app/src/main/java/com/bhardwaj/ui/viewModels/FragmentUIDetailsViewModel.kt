package com.bhardwaj.ui.viewModels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.bhardwaj.ui.adapters.ImageSliderAdapter
import com.bhardwaj.ui.models.Download
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError

class FragmentUIDetailsViewModel : ViewModel() {

    fun shareLink(link: String, activity: FragmentActivity) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        activity.startActivity(Intent.createChooser(shareIntent, "Share link via"))
    }

    fun downloadLink(url: String, activity: FragmentActivity) {
        val shareIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(Intent.createChooser(shareIntent, "Open link via"))
    }

    fun download(url: String, filename: String, mContext: Context) {
        Toast.makeText(mContext, "File downloading in background.", Toast.LENGTH_SHORT).show()
        Download(url, filename, mContext).downloadFile()
    }

    fun loadNativeAd(mContext: Context, imageSliderAdapter: ImageSliderAdapter) {
        val builder = AdLoader.Builder(mContext, "")
        builder.forNativeAd { nativeAd ->
            imageSliderAdapter.updateList(nativeAd)
        }

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                println("ad failed to load")
            }
        }).build()
        adLoader.loadAds(AdRequest.Builder().build(), 1)
    }
}