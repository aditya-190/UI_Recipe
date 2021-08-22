package com.bhardwaj.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import com.bhardwaj.ui.R
import com.bhardwaj.ui.adapters.UIListAdapter
import com.bhardwaj.ui.viewModels.FragmentSketchViewModel
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.android.material.bottomsheet.BottomSheetDialog

class FragmentSketch : Fragment(), Toolbar.OnMenuItemClickListener {
    private lateinit var uiListAdapter: UIListAdapter
    private var selectedCategory: Int? = -1
    private val viewModel: FragmentSketchViewModel by viewModels()
    private lateinit var skeletonScreen: RecyclerViewSkeletonScreen

    private lateinit var navController: NavController
    private lateinit var sketchToolbar: Toolbar
    private lateinit var sketchRefreshLayout: SwipeRefreshLayout
    private lateinit var sketchRecycler: RecyclerView

    private val loadingLiveDataObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            skeletonScreen.show()
        } else {
            sketchRefreshLayout.isRefreshing = false
            skeletonScreen.hide()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sketch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiListAdapter = UIListAdapter(
            arrayListOf(),
            getString(R.string.firestore_sketch),
            requireActivity().applicationContext
        )
        sketchRefreshLayout = view.findViewById(R.id.sketchRefreshLayout)
        sketchRecycler = view.findViewById(R.id.sketchRecycler)
        sketchToolbar = view.findViewById(R.id.sketchToolbar)
        sketchToolbar.setOnMenuItemClickListener(this)
        navController = view.findNavController()

        sketchRecycler.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = uiListAdapter
        }

        viewModel.getUIList(requireContext(), uiListAdapter)

        viewModel.loading.observe(viewLifecycleOwner, loadingLiveDataObserver)

        uiListAdapter.loadMore.observe(viewLifecycleOwner, {
            if (it) viewModel.lazyLoading(requireContext(), uiListAdapter)
        })

        skeletonScreen = Skeleton.bind(sketchRecycler)
            .adapter(uiListAdapter)
            .load(R.layout.layout_skeleton)
            .count(2)
            .show()

        sketchRefreshLayout.setOnRefreshListener {
            sketchRefreshLayout.isRefreshing = true
            context?.let { viewModel.refresh(it, uiListAdapter) }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.search -> {
                val searchView = item.actionView as SearchView
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String): Boolean {
                        return false
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        item.collapseActionView()
                        navController.navigate(
                            FragmentSketchDirections.actionFragmentSketchToFragmentSearch(
                                searchFor = query.trim(),
                                searchedBy = getString(R.string.firestore_sketch)
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