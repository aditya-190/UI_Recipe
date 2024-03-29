package com.bhardwaj.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bhardwaj.skeleton.RecyclerViewSkeletonScreen
import com.bhardwaj.skeleton.Skeleton
import com.bhardwaj.ui.R
import com.bhardwaj.ui.adapters.UIListAdapter
import com.bhardwaj.ui.viewModels.FragmentAdobeXdViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class FragmentAdobeXD : Fragment(), Toolbar.OnMenuItemClickListener {
    private lateinit var uiListAdapter: UIListAdapter
    private var selectedCategory: Int? = -1
    private val viewModel: FragmentAdobeXdViewModel by viewModels()
    private lateinit var skeletonScreen: RecyclerViewSkeletonScreen

    private lateinit var navController: NavController
    private lateinit var adobeToolbar: Toolbar
    private lateinit var adobeRefreshLayout: SwipeRefreshLayout
    private lateinit var adobeRecycler: RecyclerView

    private val loadingLiveDataObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            skeletonScreen.show()
        } else {
            adobeRefreshLayout.isRefreshing = false
            skeletonScreen.hide()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_adobe_xd, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiListAdapter = UIListAdapter(
            arrayListOf(),
            getString(R.string.firestore_xd),
            requireActivity().applicationContext
        )
        adobeRefreshLayout = view.findViewById(R.id.adobeRefreshLayout)
        adobeRecycler = view.findViewById(R.id.adobeRecycler)
        adobeToolbar = view.findViewById(R.id.adobeToolbar)
        adobeToolbar.setOnMenuItemClickListener(this)
        navController = view.findNavController()

        adobeRecycler.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = uiListAdapter
        }

        viewModel.getUIList(requireContext(), uiListAdapter)

        viewModel.loading.observe(viewLifecycleOwner, loadingLiveDataObserver)

        uiListAdapter.loadMore.observe(viewLifecycleOwner) {
            if (it) viewModel.lazyLoading(requireContext(), uiListAdapter)
        }

        skeletonScreen = Skeleton.bind(adobeRecycler)
            ?.adapter(uiListAdapter)
            ?.load(R.layout.layout_skeleton)
            ?.count(2)
            ?.show()!!

        adobeRefreshLayout.setOnRefreshListener {
            adobeRefreshLayout.isRefreshing = true
            context?.let { viewModel.refresh(it, uiListAdapter) }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.search -> {
                val searchView = item.actionView as SearchView
                val closeImage: ImageView =
                    searchView.findViewById(androidx.appcompat.R.id.search_close_btn)
                closeImage.setImageResource(R.drawable.icon_close)

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String): Boolean {
                        return false
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        item.collapseActionView()
                        navController.navigate(
                            FragmentAdobeXDDirections.actionFragmentAdobeXDToFragmentSearch(
                                query.trim(),
                                getString(R.string.firestore_xd)
                            )
                        )
                        return false
                    }
                })
            }

            R.id.filter -> {
                activity?.let {
                    val dialog = BottomSheetDialog(it)
                    dialog.setContentView(R.layout.dialog_filter)

                    val rgFilterOptions: RadioGroup? = dialog.findViewById(R.id.rgFilterOptions)
                    val tvApplyButton: TextView? = dialog.findViewById(R.id.tvApplyButton)

                    tvApplyButton?.setOnClickListener {
                        selectedCategory = rgFilterOptions?.checkedRadioButtonId
                        when (selectedCategory) {
                            R.id.rbAll -> {
                                viewModel.filter(
                                    getString(R.string.all),
                                    requireContext(),
                                    uiListAdapter
                                )
                                dialog.dismiss()
                            }
                            R.id.rbMobile -> {
                                viewModel.filter(
                                    getString(R.string.mobile),
                                    requireContext(),
                                    uiListAdapter
                                )
                                dialog.dismiss()
                            }
                            R.id.rbWebsite -> {
                                viewModel.filter(
                                    getString(R.string.website),
                                    requireContext(),
                                    uiListAdapter
                                )
                                dialog.dismiss()
                            }
                            R.id.rbMiscellaneous -> {
                                viewModel.filter(
                                    getString(R.string.miscellaneous),
                                    requireContext(),
                                    uiListAdapter
                                )
                                dialog.dismiss()
                            }
                            else -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.category_not_found),
                                    Toast.LENGTH_SHORT
                                ).show()
                                selectedCategory = -1
                            }
                        }
                    }
                    dialog.show()
                }
            }
        }
        return true
    }
}