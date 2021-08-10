package com.bhardwaj.ui.utils

import android.view.View

interface UIDetailListener {
    fun onUIListClick(view: View)
}

interface call {
    fun onCall(uiListItem: ArrayList<Any>)
}