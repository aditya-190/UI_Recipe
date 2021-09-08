package com.bhardwaj.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bhardwaj.ui.R
import com.bhardwaj.ui.utils.Util
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class ImageSliderAdapter(
    var mContext: Context,
    private var imageSliderList: ArrayList<Any>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder0(var view: View) : RecyclerView.ViewHolder(view) {
        val fullImage: PhotoView = view.findViewById(R.id.full_image)
    }

    class ViewHolder1(var view: View) : RecyclerView.ViewHolder(view) {
        val adView: NativeAdView = view.findViewById(R.id.ad_view)
    }

    fun updateList(item: NativeAd) {
        var postion: Int = imageSliderList.size
        postion = (postion / 2) + 1
        imageSliderList.add(postion, item)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                ViewHolder1(
                    LayoutInflater.from(mContext).inflate(R.layout.layout_ads_medium, parent, false)
                )
            }

            else -> {
                ViewHolder0(
                    LayoutInflater.from(mContext)
                        .inflate(R.layout.layout_full_image, parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            1 -> {
                val holder1: ViewHolder1 = holder as ViewHolder1
                val nativeAd = imageSliderList[position] as NativeAd
                Util.populateNativeAdView(nativeAd, holder1.adView)
            }

            0 -> {
                val holder0: ViewHolder0 = holder as ViewHolder0
                val progressDrawable = Util.getProgressDrawable(mContext)
                progressDrawable.setColorSchemeColors(mContext.getColor(R.color.colorOnPrimary))
                Glide.with(mContext)
                    .setDefaultRequestOptions(
                        RequestOptions()
                            .placeholder(progressDrawable)
                            .fitCenter()
                    )
                    .load(imageSliderList[position] as String)
                    .into(holder0.fullImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return imageSliderList.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = imageSliderList[position]
        if (item is NativeAd) return 1
        return 0
    }
}