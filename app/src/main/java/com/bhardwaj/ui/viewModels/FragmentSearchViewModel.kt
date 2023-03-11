package com.bhardwaj.ui.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bhardwaj.ui.R
import com.bhardwaj.ui.models.FirestoreRepository
import com.bhardwaj.ui.models.UI
import com.google.firebase.firestore.ktx.toObject
import java.util.*

class FragmentSearchViewModel : ViewModel() {
    private var firebaseRepository = FirestoreRepository()
    private var queryList: ArrayList<String> = arrayListOf()
    val uiList by lazy { MutableLiveData<ArrayList<UI>>() }
    val loading by lazy { MutableLiveData<Boolean>() }

    fun getSearchResults(type: String, query: String, mContext: Context) {
        loading.value = true
        query.lowercase(Locale.ENGLISH)

        val searchQuery = query.split("\\s".toRegex())
        queryList.addAll(searchQuery)

        firebaseRepository.getSearchResults(type, queryList)
            .addOnSuccessListener { result ->
                val itemList = arrayListOf<UI>()
                if (result.isEmpty) {
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
                uiList.value = itemList
                loading.value = false
            }.addOnFailureListener {
                loading.value = false
            }
    }
}