package com.example.cal2hubapp

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cal2hubapp.data.AuthRepository
import com.example.cal2hubapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        authRepository = AuthRepository(this)
        navController = findNavController(R.id.navHostFragment)

        // Include all top-level destinations (shown in drawer)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.mainFragment,
                R.id.myAccountFragment,
                R.id.myPerformanceFragment,
                R.id.aboutFragment
            ),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navigationView.setupWithNavController(navController)

        // Handle drawer menu clicks
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.myAccountFragment -> {
                    navController.navigate(R.id.myAccountFragment)
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.myPerformanceFragment -> {
                    navController.navigate(R.id.myPerformanceFragment)
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.aboutFragment -> {
                    navController.navigate(R.id.aboutFragment)
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.exitApplication -> {
                    showExitConfirmationDialog()
                    true
                }
                else -> false
            }
        }

        // Auth guard — redirect to splash/login if not logged in
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (isProtectedDestination(destination.id) && !authRepository.isLoggedIn()) {
                navController.navigate(R.id.splashFragment)
            }

            // ✅ Hide toolbar and drawer on splash/login screen
            if (destination.id == R.id.splashFragment) {
                hideSystemUI()
                binding.toolbar.visibility = View.GONE
                binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                showSystemUI()
                binding.toolbar.visibility = View.VISIBLE
                binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Protected destinations (require login)
    private fun isProtectedDestination(destinationId: Int): Boolean {
        return destinationId in listOf(
            R.id.mainFragment,
            R.id.competenciesFragment,
            R.id.lectureFragment,
            R.id.quizFragment,
            R.id.quizResultFragment,
            R.id.masterExamFragment,
            R.id.congratsFragment,
            R.id.myAccountFragment,
            R.id.myPerformanceFragment,
            R.id.aboutFragment
        )
    }

    // Exit confirmation dialog
    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit Application")
            .setMessage("Are you sure you want to exit the app?")
            .setPositiveButton("Yes") { _, _ ->
                finishAffinity() // closes all activities
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Fullscreen / hide status & navigation bars
    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }

    // Show system bars again
    private fun showSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }
}
