package com.bhardwaj.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bhardwaj.ui.R
import com.bhardwaj.ui.adapters.ImageSliderAdapter
import com.bhardwaj.ui.models.UI
import com.bhardwaj.ui.utils.UIDetailListener
import com.bhardwaj.ui.viewModels.FragmentUIDetailsViewModel
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class FragmentUIDetails : Fragment(), UIDetailListener {

    private lateinit var viewModel: FragmentUIDetailsViewModel
    private lateinit var sliderAdapter: ImageSliderAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var uiDetails: UI
    private var itemList: ArrayList<Any> = arrayListOf()
    private val args: FragmentUIDetailsArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        uiDetails = args.uIDetail
        return inflater.inflate(R.layout.fragment_ui_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPager)
        dotsIndicator = view.findViewById(R.id.dotsIndicator)

        viewModel = ViewModelProvider(this).get(FragmentUIDetailsViewModel::class.java)

        itemList.addAll(uiDetails.uiImages!!)
        sliderAdapter = context?.let { ImageSliderAdapter(it, itemList) }!!
        context?.let {
            viewModel.loadNativeAd(it, sliderAdapter)
        }

        viewPager.adapter = sliderAdapter
        dotsIndicator.setViewPager2(viewPager)
    }

    private fun showInfo() {
        activity?.let {
            AlertDialog.Builder(it).apply {
                val dialogView =
                    requireActivity().layoutInflater.inflate(R.layout.dialog_ui_information, null)

                val designer = dialogView.findViewById<TextView>(R.id.tvDesignerInfo)
                val source = dialogView.findViewById<TextView>(R.id.tvSourceInfo)

                designer.text =
                    String.format("%s: %s", getString(R.string.designer), uiDetails.author)
                source.text =
                    String.format("%s: %s", getString(R.string.source), uiDetails.source)

                setView(dialogView)
            }.create().show()
        }
    }

    override fun onUIListClick(view: View) {
        when (view.id) {
            R.id.ivInfo -> showInfo()

            R.id.ivShareUI -> viewModel.shareLink(
                uiDetails.downloadUrl.toString(),
                requireActivity()
            )

            R.id.fabButton -> viewModel.download(
                uiDetails.downloadUrl.toString(),
                uiDetails.fileName!!,
                requireContext()
            )
        }
    }
}