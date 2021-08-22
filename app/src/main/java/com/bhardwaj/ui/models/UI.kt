package com.bhardwaj.ui.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
class UI(
    val imageUrl: String? = null,
    val title: String? = null,
    val author: String? = null,
    val downloadUrl: String? = null,
    val fileName: String? = null,
    val source: String? = null,
    val timeStamp: Long? = 0,
    val category: String? = null,
    val tag: List<String>? = null,
    val uiImages: List<String>? = null
) : Parcelable