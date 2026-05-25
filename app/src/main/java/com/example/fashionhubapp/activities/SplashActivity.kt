package com.example.fashionhubapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionhubapp.R
import com.example.fashionhubapp.activities.admin.AdminDashboardActivity
import com.example.fashionhubapp.activities.auth.LoginActivity
import com.example.fashionhubapp.activities.user.UserDashboardActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        // SPLASH DELAY
        Handler(Looper.getMainLooper()).postDelayed({

            // SHARED PREFERENCES
            val pref = getSharedPreferences(
                "FashionHub",
                MODE_PRIVATE
            )

            val token = pref.getString("token", null)

            val gid = pref.getInt("gid", 0)

            // CHECK LOGIN
            if (token != null) {

                // ADMIN
                if (gid == 2) {

                    startActivity(
                        Intent(
                            this@SplashActivity,
                            AdminDashboardActivity::class.java
                        )
                    )

                }
                // USER
                else {

                    startActivity(
                        Intent(
                            this@SplashActivity,
                            UserDashboardActivity::class.java
                        )
                    )
                }

            }
            // NOT LOGIN
            else {

                startActivity(
                    Intent(
                        this@SplashActivity,
                        LoginActivity::class.java
                    )
                )
            }

            finish()

        }, 2500)
    }
}