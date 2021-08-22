package com.bhardwaj.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bhardwaj.ui.R
import com.bhardwaj.ui.models.UI
import com.bhardwaj.ui.utils.Call
import com.bhardwaj.ui.utils.Util
import com.bhardwaj.ui.view.FragmentAdobeXDDirections
import com.bhardwaj.ui.view.FragmentFigmaDirections
import com.bhardwaj.ui.view.FragmentSketchDirections
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.card.MaterialCardView

class UIListAdapter(
    val uiList: ArrayList<Any>,
    private val type: String,
    private val mContext: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Call {

    val loadMore by lazy { MutableLiveData<Boolean>() }

    class ViewHolder0(var view: View) : RecyclerView.ViewHolder(view) {
        val mcvUIContainer: MaterialCardView = view.findViewById(R.id.mcvUIContainer)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val ivImage: ImageView = view.findViewById(R.id.ivImage)
    }

    class ViewHolder1(var view: View) : RecyclerView.ViewHolder(view) {
        val adView: NativeAdView = view.findViewById(R.id.ad_view)
    }

    fun updateList(newUiList: ArrayList<Any>) {
        uiList.clear()
        uiList.addAll(newUiList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                ViewHolder1(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_ads_small, parent, false)
                )
            }
            else -> {
                ViewHolder0(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_single_item, parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (position == uiList.size - 4) loadMore.value = true

        when (viewType) {
            1 -> {
                val holder1: ViewHolder1 = holder as ViewHolder1
                Util.populateNativeAdView(uiList[position] as NativeAd, holder1.adView)
            }

            0 -> {
                val holder0: ViewHolder0 = holder as ViewHolder0
                val current = uiList[position] as UI

                Glide.with(mContext)
                    .setDefaultRequestOptions(RequestOptions())
                    .load(current.imageUrl)
                    .into(holder0.ivImage)

                holder0.tvTitle.text = current.title

                holder0.mcvUIContainer.setOnClickListener { view ->
                    when (type) {
                        mContext.getString(R.string.firestore_sketch) -> {
                            Navigation.findNavController(view).navigate(
                                FragmentSketchDirections.actionFragmentSketchToFragmentUIDetails(
                                    current
                                )
                            )
                        }

                        mContext.getString(R.string.firestore_figma) -> {
                            Navigation.findNavController(view).navigate(
                                FragmentFigmaDirections.actionFragmentFigmaToFragmentUIDetails(
                                    current
                                )
                            )
                        }
                        else -> {
                            Navigation.findNavController(view).navigate(
                                FragmentAdobeXDDirections.actionFragmentAdobeXDToFragmentUIDetails(
                                    current
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return uiList.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = uiList[position]
        if (item is NativeAd) return 1
        return 0
    }

    override fun onCall(uiListItem: ArrayList<Any>) {
        uiList.addAll(uiListItem)
        notifyDataSetChanged()
    }
}