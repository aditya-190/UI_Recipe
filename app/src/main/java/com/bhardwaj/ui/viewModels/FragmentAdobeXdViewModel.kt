package com.bhardwaj.ui.viewModels

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bhardwaj.ui.R
import com.bhardwaj.ui.adapters.UIListAdapter
import com.bhardwaj.ui.models.FirestoreRepository
import com.bhardwaj.ui.models.UI
import com.bhardwaj.ui.utils.Util.Companion.intervalOfAds
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject

class FragmentAdobeXdViewModel : ViewModel() {

    private lateinit var lazyLoadQuery: DocumentSnapshot
    private var firebaseRepository = FirestoreRepository()
    private lateinit var unifiedAds: NativeAd
    private val itemList: ArrayList<Any> = arrayListOf()
    val loading by lazy { MutableLiveData<Boolean>() }

    private var indexDefaultValue: Int = 5
    private var indexForAd: Int = indexDefaultValue
    private val startShowingAds = true // Change Here to Show Ads in UI List Adapter

    fun filter(category: String, mContext: Context, uiListAdapter: UIListAdapter) {
        loading.value = true
        firebaseRepository.filterUIList(category, mContext.getString(R.string.firestore_xd))
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.no_results),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                uiListAdapter.uiList.clear()
                for (item in result) {
                    val uiItem = item.toObject<UI>()
                    uiListAdapter.uiList.add(uiItem)
                }
                loading.value = false
                if (startShowingAds) loadNativeAd(mContext, uiListAdapter)
            }.addOnFailureListener {
                loading.value = false
            }
    }

    private fun insertAds(uiListAdapter: UIListAdapter) {
        uiListAdapter.uiList.add(indexForAd, unifiedAds)
        uiListAdapter.notifyItemInserted(indexForAd)
    }

    private fun loadNativeAd(mContext: Context, uiListAdapter: UIListAdapter) {
        var adLoader: AdLoader? = null
        val builder =
            AdLoader.Builder(mContext, mContext.resources.getString(R.string.adobe_xd_ads))
        adLoader = builder.forNativeAd { nativeAd ->
            unifiedAds = nativeAd

            if (!adLoader!!.isLoading) {
                insertAds(uiListAdapter)
            }

        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                println("ad failed to load")
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun refresh(mContext: Context, uiListAdapter: UIListAdapter) {
        getUIList(mContext, uiListAdapter)
    }

    private fun isNetworkAvailable(mContext: Context) =
        with(mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager) {
            getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } ?: false
        }

    fun getUIList(mContext: Context, uiListAdapter: UIListAdapter) {
        if (isNetworkAvailable(mContext)) {
            loading.value = true
            itemList.clear()
            firebaseRepository.getUIList(mContext.getString(R.string.firestore_xd))
                .addOnSuccessListener { result ->
                    if (result.documents.size > 0) {
                        lazyLoadQuery = result.documents[result.size() - 1]
                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.getString(R.string.no_results),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    for (item in result) {
                        val uiItem = item.toObject<UI>()
                        itemList.add(uiItem)
                    }
                    uiListAdapter.updateList(itemList)
                    loading.value = false
                    indexForAd = indexDefaultValue
                    if (startShowingAds) loadNativeAd(mContext, uiListAdapter)
                }.addOnFailureListener {
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.fetch_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(
                mContext,
                mContext.getString(R.string.no_internet_error),
                Toast.LENGTH_SHORT
            ).show()
            loading.value = false
        }
    }

    fun lazyLoading(mContext: Context, uiListAdapter: UIListAdapter) {
        firebaseRepository.lazyLoading(mContext.getString(R.string.firestore_xd), lazyLoadQuery)
            .addOnSuccessListener { result ->
                if (result.documents.size > 0) {
                    lazyLoadQuery = result.documents[result.size() - 1]
                }
                itemList.clear()
                for (item in result) {
                    val uiItem = item.toObject<UI>()
                    itemList.add(uiItem)
                }
                uiListAdapter.uiList.addAll(itemList)
                uiListAdapter.notifyDataSetChanged()
                uiListAdapter.loadMore.value = false
                indexForAd += intervalOfAds
                if (startShowingAds) loadNativeAd(mContext, uiListAdapter)
            }.addOnFailureListener {
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.fetch_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}