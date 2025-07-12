package com.example.crm_application

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.crm_application.ui.auth.LoginActivity
import com.example.crm_application.ui.dashboard.DashboardFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.core.view.isVisible
import com.example.crm_application.ui.documents.DocumentFragment
import com.example.crm_application.ui.leads.LeadsFragment
import com.example.crm_application.ui.tasks.TasksFragment
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var overlay: View
    private lateinit var signOutButton: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.coordinator_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check authentication token
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = prefs.getString("access_token", null)
        val key = prefs.getString("key", null)

        if (token.isNullOrEmpty() and key.isNullOrEmpty()) {
            Toast.makeText(this, "Authentication error!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Initialize bottom navigation
        bottomNavigation = findViewById(R.id.bottomNav)

        // Initialize Bottom Sheet
        val bottomSheet: LinearLayout = findViewById(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        signOutButton = findViewById(R.id.signOutButton)

        // Overlay setup
        overlay = findViewById(R.id.bottom_sheet_overlay)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED,
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        overlay.visibility = View.VISIBLE
                        overlay.alpha = 1f
                    }

                    BottomSheetBehavior.STATE_COLLAPSED,
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                        overlay.alpha = 0f
                    }

                    else -> {
                        overlay.visibility = View.VISIBLE
                        overlay.alpha = 1f
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // You can comment this out or leave it if you still want a slight fade effect.
                // overlay.alpha = slideOffset.coerceIn(0f, 1f)
            }
        })

        overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        // Set navigation item selected listener
        bottomNavigation.setOnItemSelectedListener { item ->
            handleNavigationClick(item)
        }

        signOutButton.setOnClickListener {
            logout()
        }

        // Load default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.nav_host_fragment, DashboardFragment())
            }
            bottomNavigation.selectedItemId = R.id.nav_home
        }
    }

    private fun handleNavigationClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.commit {
                    replace(R.id.nav_host_fragment, DashboardFragment())
                }
                true
            }

            R.id.nav_documents -> {
                supportFragmentManager.commit {
                    replace(R.id.nav_host_fragment, DocumentFragment())
                }
                true
            }

            R.id.nav_tasks -> {
                supportFragmentManager.commit {
                    replace(R.id.nav_host_fragment, TasksFragment())
                }
                true
            }

            R.id.nav_client -> {
                supportFragmentManager.commit {
                    replace(R.id.nav_host_fragment, LeadsFragment())
                }
                true
            }

            R.id.nav_more -> {
                toggleBottomSheet()
                false
            }

            else -> false
        }
    }

    private fun toggleBottomSheet() {
        bottomSheetBehavior.state = if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            BottomSheetBehavior.STATE_EXPANDED
        } else {
            BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun logout() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit { clear() }

        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}