package com.bhardwaj.ui.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bhardwaj.ui.R
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private var canShowSnackBar: Boolean = false
    private lateinit var clRootMain: CoordinatorLayout
    private lateinit var navController: NavController
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}

        // TODO [ADITYA]: Remove these before release
        val testDeviceIds = listOf("0BF60C991231FA0846B18363CAA632EA")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)

        initialise()
    }

    private fun initialise() {
        clRootMain = findViewById(R.id.clRootMain)
        bottomNavigation = findViewById(R.id.bottomNavigationBar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentMain) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.fragmentAdobeXD -> {
                bottomNavigation.visibility = View.VISIBLE
                canShowSnackBar = true
            }

            R.id.fragmentFigma, R.id.fragmentSketch, R.id.fragmentMore -> {
                bottomNavigation.visibility = View.VISIBLE
                canShowSnackBar = false
            }

            else -> {
                bottomNavigation.visibility = View.GONE
                canShowSnackBar = false
            }
        }
    }

    override fun onBackPressed() {
        if (canShowSnackBar) {
            Snackbar.make(clRootMain, getString(R.string.exit), Snackbar.LENGTH_SHORT)
                .also {
                    it.setAction("Yes") { ActivityCompat.finishAffinity(this) }
                    it.setActionTextColor(resources.getColor(R.color.colorWhite, this.theme))
                }.show()
        } else {
            super.onBackPressed()
        }
    }
}