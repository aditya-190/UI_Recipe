package com.bhardwaj.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.bhardwaj.ui.R
import com.bhardwaj.ui.utils.UIDetailListener
import com.bhardwaj.ui.viewModels.FragmentMoreViewModel

class FragmentMore : Fragment(), UIDetailListener {

    private lateinit var navController: NavController
    private lateinit var viewModel: FragmentMoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()
    }

    override fun onUIListClick(view: View) {
        when (view.id) {
            R.id.tvRateApp -> viewModel.rateApp(requireContext())
            R.id.tvPrivacy -> viewModel.privacyPolicy(requireContext())
            R.id.tvAbout -> viewModel.about(requireContext())
            R.id.tvContact -> viewModel.contact(requireActivity())
        }
    }
}