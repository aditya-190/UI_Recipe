package com.bhardwaj.ui.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bhardwaj.ui.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private var canShowSnackBar: Boolean = false
    private lateinit var clRootMain: ConstraintLayout
    private lateinit var navController: NavController
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialise()
    }

    private fun initialise() {
        clRootMain = findViewById(R.id.clRootMain)
        bottomNavigation = findViewById(R.id.bottomNavigationBar)

        navController = findNavController(R.id.fragmentMain)
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
            Snackbar.make(clRootMain, "Are you sure you want to exit?", Snackbar.LENGTH_SHORT)
                .also {
                    it.setAction("Yes") { ActivityCompat.finishAffinity(this) }
                    it.setActionTextColor(resources.getColor(R.color.colorWhite, this.theme))
                }.show()
        } else {
            super.onBackPressed()
        }
    }
}