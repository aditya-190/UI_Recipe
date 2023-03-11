package com.bhardwaj.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bhardwaj.ui.R
import com.bhardwaj.ui.adapters.SearchAdapter
import com.bhardwaj.ui.models.UI
import com.bhardwaj.ui.viewModels.FragmentSearchViewModel
import com.bhardwaj.skeleton.RecyclerViewSkeletonScreen
import com.bhardwaj.skeleton.Skeleton

class FragmentSearch : Fragment() {

    private val args: FragmentSearchArgs by navArgs()
    private val viewModel: FragmentSearchViewModel by viewModels()
    private lateinit var uiListAdapter: SearchAdapter
    private lateinit var skeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var searchFor: String
    private lateinit var searchedBy: String
    private lateinit var searchRecycler: RecyclerView

    private val uiListDataObserver = Observer<ArrayList<UI>> { list ->
        list.let {
            searchRecycler.visibility = View.VISIBLE
            uiListAdapter.updateList(it)
        }
    }

    private val loadingLiveDataObserver = Observer<Boolean> { isLoading ->
        if (isLoading) skeletonScreen.show()
        else skeletonScreen.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchFor = args.searchFor
        searchedBy = args.searchedBy
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchRecycler = view.findViewById(R.id.searchRecycler)

        uiListAdapter = SearchAdapter(requireContext(), arrayListOf())
        viewModel.uiList.observe(viewLifecycleOwner, uiListDataObserver)
        viewModel.loading.observe(viewLifecycleOwner, loadingLiveDataObserver)

        searchRecycler.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = uiListAdapter
        }

        viewModel.getSearchResults(
            mContext = requireContext(),
            type = searchedBy,
            query = searchFor,
        )

        skeletonScreen = Skeleton.bind(searchRecycler)
            ?.adapter(uiListAdapter)
            ?.load(R.layout.layout_skeleton)
            ?.count(2)
            ?.show()!!
    }
}