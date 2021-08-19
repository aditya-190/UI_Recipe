package com.bhardwaj.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import com.bhardwaj.ui.viewModels.FragmentFigmaViewModel
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton

class FragmentFigma : Fragment(), Toolbar.OnMenuItemClickListener {
    private lateinit var uiListAdapter: UIListAdapter
    private var selectedCategory: Int = -1
    private val viewModel: FragmentFigmaViewModel by viewModels()
    private lateinit var skeletonScreen: RecyclerViewSkeletonScreen

    private lateinit var navController: NavController
    private lateinit var figmaToolbar: Toolbar
    private lateinit var figmaRefreshLayout: SwipeRefreshLayout
    private lateinit var figmaRecycler: RecyclerView

    private val loadingLiveDataObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            skeletonScreen.show()
        } else {
            figmaRefreshLayout.isRefreshing = false
            skeletonScreen.hide()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.listState = figmaRecycler.layoutManager?.onSaveInstanceState()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_figma, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiListAdapter = UIListAdapter(arrayListOf(), "figma", requireActivity().applicationContext)
        figmaRefreshLayout = view.findViewById(R.id.figmaRefreshLayout)
        figmaRecycler = view.findViewById(R.id.figmaRecycler)
        figmaToolbar = view.findViewById(R.id.figmaToolbar)
        figmaToolbar.setOnMenuItemClickListener(this)
        navController = view.findNavController()

        figmaRecycler.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = uiListAdapter
        }

        if (viewModel.listState != null) {
            figmaRecycler.layoutManager?.onRestoreInstanceState(viewModel.listState)
            viewModel.listState = null
        } else {
            viewModel.getUIList(requireContext(), uiListAdapter)
        }

        viewModel.loading.observe(viewLifecycleOwner, loadingLiveDataObserver)

        uiListAdapter.loadMore.observe(viewLifecycleOwner, {
            if (it) viewModel.lazyLoading(requireContext(), uiListAdapter)
        })

        skeletonScreen = Skeleton.bind(figmaRecycler)
            .adapter(uiListAdapter)
            .load(R.layout.layout_skeleton)
            .count(2)
            .show()

        figmaRefreshLayout.setOnRefreshListener {
            figmaRefreshLayout.isRefreshing = true
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
                        navController.navigate(FragmentFigmaDirections.actionFragmentFigmaToFragmentSearch())
                        return false
                    }
                })
            }

            R.id.filter -> {
                Toast.makeText(
                    requireActivity().applicationContext,
                    "Filter Selected.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return true
    }
}