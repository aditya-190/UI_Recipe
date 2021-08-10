package com.bhardwaj.ui.utils

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bhardwaj.ui.R
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class Util {
    companion object {
        fun populateNativeAdView(
            nativeAd: NativeAd,
            adView: NativeAdView
        ) {
            adView.mediaView = adView.findViewById(R.id.ad_media)
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            (adView.headlineView as TextView).text = nativeAd.headline
            adView.mediaView?.setMediaContent(nativeAd.mediaContent!!)

            adView.bodyView = adView.findViewById(R.id.ad_body)
            if (nativeAd.body == null) {
                adView.bodyView?.visibility = View.GONE
            } else {
                adView.bodyView?.visibility = View.VISIBLE
                (adView.bodyView as TextView).text = nativeAd.body
            }

            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            if (nativeAd.callToAction == null) {
                adView.callToActionView?.visibility = View.INVISIBLE
            } else {
                adView.callToActionView?.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }

            adView.iconView = adView.findViewById(R.id.ad_icon)
            if (nativeAd.icon == null) {
                adView.iconView?.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon?.drawable
                )
                adView.iconView?.visibility = View.VISIBLE
            }

            adView.priceView = adView.findViewById(R.id.ad_price)
            if (nativeAd.price == null) {
                adView.priceView?.visibility = View.INVISIBLE
            } else {
                adView.priceView?.visibility = View.VISIBLE
                (adView.priceView as TextView).text = nativeAd.price
            }

            adView.storeView = adView.findViewById(R.id.ad_store)
            if (nativeAd.store == null) {
                adView.storeView?.visibility = View.INVISIBLE
            } else {
                adView.storeView?.visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }

            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            if (nativeAd.starRating == null) {
                adView.starRatingView?.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                adView.starRatingView?.visibility = View.VISIBLE
            }

            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
            if (nativeAd.advertiser == null) {
                adView.advertiserView?.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView?.visibility = View.VISIBLE
            }
            adView.setNativeAd(nativeAd)
        }

        fun getProgressDrawable(mContext: Context): CircularProgressDrawable {
            return CircularProgressDrawable(mContext).apply {
                strokeWidth = 10f
                centerRadius = 40f
                start()
            }
        }
    }
}