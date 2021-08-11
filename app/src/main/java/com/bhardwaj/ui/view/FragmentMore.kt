package com.bhardwaj.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.bhardwaj.ui.R
import com.bhardwaj.ui.viewModels.FragmentMoreViewModel

class FragmentMore : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController
    private lateinit var viewModel: FragmentMoreViewModel
    private lateinit var tvRateApp: TextView
    private lateinit var tvPrivacy: TextView
    private lateinit var tvAbout: TextView
    private lateinit var tvContact: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvRateApp = view.findViewById(R.id.tvRateApp)
        tvPrivacy = view.findViewById(R.id.tvPrivacy)
        tvAbout = view.findViewById(R.id.tvAbout)
        tvContact = view.findViewById(R.id.tvContact)

        viewModel = ViewModelProvider(this).get(FragmentMoreViewModel::class.java)
        navController = view.findNavController()

        tvRateApp.setOnClickListener(this)
        tvPrivacy.setOnClickListener(this)
        tvAbout.setOnClickListener(this)
        tvContact.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tvRateApp -> viewModel.rateApp(requireContext())
            R.id.tvPrivacy -> viewModel.privacyPolicy(requireContext())
            R.id.tvAbout -> viewModel.about(requireContext())
            R.id.tvContact -> viewModel.contact(requireActivity())
        }
    }
}