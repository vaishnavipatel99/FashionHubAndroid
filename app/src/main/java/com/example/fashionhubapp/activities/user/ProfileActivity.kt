package com.example.fashionhubapp.activities.user

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionhubapp.R
import com.example.fashionhubapp.activities.auth.LoginActivity
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.UserProfile
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMobile: EditText
    private lateinit var etCity: EditText
    private lateinit var etState: EditText

    private lateinit var btnUpdate: Button

    private lateinit var bottomNavigation: BottomNavigationView

    private var userId = 0

    private lateinit var currentUser: UserProfile

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        // =========================
        // STATUS BAR COLOR
        // =========================

        window.statusBarColor =
            getColor(android.R.color.white)

        window.navigationBarColor =
            getColor(android.R.color.white)

        // =========================
        // FIND VIEW
        // =========================

        etName =
            findViewById(R.id.etName)

        etEmail =
            findViewById(R.id.etEmail)

        etMobile =
            findViewById(R.id.etMobile)

        etCity =
            findViewById(R.id.etCity)

        etState =
            findViewById(R.id.etState)

        btnUpdate =
            findViewById(R.id.btnUpdate)

        bottomNavigation =
            findViewById(R.id.bottomNavigation)

        val btnBack =
            findViewById<ImageView>(R.id.btnBack)

        // =========================
        // GET USER ID
        // =========================

        val pref =
            getSharedPreferences(
                "FashionHub",
                MODE_PRIVATE
            )

        userId =
            pref.getInt("uid", 0)

        // =========================
        // BACK BUTTON
        // =========================

        btnBack.setOnClickListener {

            finish()
        }

        // =========================
        // LOAD PROFILE
        // =========================

        getUserProfile()

        // =========================
        // UPDATE BUTTON
        // =========================

        btnUpdate.setOnClickListener {

            updateProfile()
        }

        // =========================
        // BOTTOM NAVIGATION
        // =========================

        bottomNavigation.selectedItemId =
            R.id.nav_profile

        bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {

                // HOME

                R.id.nav_home -> {

                    startActivity(
                        Intent(
                            this,
                            UserDashboardActivity::class.java
                        )
                    )

                    true
                }

                // CART

                R.id.nav_cart -> {

                    startActivity(
                        Intent(
                            this,
                            CartActivity::class.java
                        )
                    )

                    true
                }

                // ORDERS

                R.id.nav_orders -> {

                    startActivity(
                        Intent(
                            this,
                            OrdersActivity::class.java
                        )
                    )

                    true
                }

                // PROFILE

                R.id.nav_profile -> true

                // LOGOUT

                R.id.nav_logout -> {

                    pref.edit().clear().apply()

                    startActivity(
                        Intent(
                            this,
                            LoginActivity::class.java
                        )
                    )

                    finish()

                    true
                }

                else -> false
            }
        }
    }

    // =========================
    // GET USER PROFILE
    // =========================

    private fun getUserProfile() {

        RetrofitClient.instance
            .getUserById(userId)

            .enqueue(object : Callback<UserProfile> {

                override fun onResponse(
                    call: Call<UserProfile>,
                    response: Response<UserProfile>
                ) {

                    if (
                        response.isSuccessful &&
                        response.body() != null
                    ) {

                        currentUser =
                            response.body()!!

                        etName.setText(
                            currentUser.username
                        )

                        etEmail.setText(
                            currentUser.email
                        )

                        etMobile.setText(
                            currentUser.mobileno
                        )

                        etCity.setText(
                            currentUser.city
                        )

                        etState.setText(
                            currentUser.state
                        )
                    }
                }

                override fun onFailure(
                    call: Call<UserProfile>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@ProfileActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // =========================
    // UPDATE PROFILE
    // =========================

    private fun updateProfile() {

        val name =
            etName.text.toString().trim()

        val email =
            etEmail.text.toString().trim()

        val mobile =
            etMobile.text.toString().trim()

        val city =
            etCity.text.toString().trim()

        val state =
            etState.text.toString().trim()

        // =========================
        // VALIDATION
        // =========================

        if (name.isEmpty()) {

            etName.error =
                "Enter Name"

            return
        }

        if (email.isEmpty()) {

            etEmail.error =
                "Enter Email"

            return
        }

        if (mobile.isEmpty()) {

            etMobile.error =
                "Enter Mobile"

            return
        }

        // =========================
        // UPDATE MODEL
        // =========================

        currentUser.username =
            name

        currentUser.email =
            email

        currentUser.mobileno =
            mobile

        currentUser.city =
            city

        currentUser.state =
            state

        // =========================
        // API CALL
        // =========================

        RetrofitClient.instance

            .updateUser(
                userId,
                currentUser
            )

            .enqueue(object : Callback<Any> {

                override fun onResponse(
                    call: Call<Any>,
                    response: Response<Any>
                ) {

                    if (response.isSuccessful) {

                        // SAVE UPDATED NAME

                        val pref =
                            getSharedPreferences(
                                "FashionHub",
                                MODE_PRIVATE
                            )

                        pref.edit()

                            .putString(
                                "name",
                                name
                            )

                            .apply()

                        Toast.makeText(
                            this@ProfileActivity,
                            "Profile Updated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        Toast.makeText(
                            this@ProfileActivity,
                            "Update Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<Any>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@ProfileActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}