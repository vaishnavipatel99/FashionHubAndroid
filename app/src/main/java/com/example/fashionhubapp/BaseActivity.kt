package com.example.fashionhubapp

import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

open class BaseActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar

    fun setupDrawer(title: String) {

        drawerLayout = findViewById(R.id.drawerLayout)

        navigationView = findViewById(R.id.navigationView)

        toolbar = findViewById(R.id.toolbar)

        toolbar.title = title

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        )

        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(
        item: MenuItem
    ): Boolean {

        when (item.itemId) {

            R.id.nav_home -> {

                if (this !is UserDashboardActivity) {

                    startActivity(
                        Intent(
                            this,
                            UserDashboardActivity::class.java
                        )
                    )
                }
            }

            R.id.nav_cart -> {

                if (this !is CartActivity) {

                    startActivity(
                        Intent(
                            this,
                            CartActivity::class.java
                        )
                    )
                }
            }

            R.id.nav_profile -> {

                if (this !is ProfileActivity) {

                    startActivity(
                        Intent(
                            this,
                            ProfileActivity::class.java
                        )
                    )
                }
            }

            R.id.nav_logout -> {

                getSharedPreferences(
                    "fashionhub",
                    MODE_PRIVATE
                ).edit().clear().apply()

                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    )
                )

                finish()
            }
        }

        drawerLayout.closeDrawer(
            GravityCompat.START
        )

        return true
    }
}