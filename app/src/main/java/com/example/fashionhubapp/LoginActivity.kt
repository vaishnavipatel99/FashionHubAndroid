package com.example.fashionhubapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.LoginRequest
import com.example.fashionhubapp.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginBtn: Button
    private lateinit var goRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // =========================
        // FIND VIEWS
        // =========================

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        loginBtn = findViewById(R.id.loginBtn)
        goRegister = findViewById(R.id.goRegister)

        val api = RetrofitClient.instance

        // =========================
        // REGISTER PAGE
        // =========================

        goRegister.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }

        // =========================
        // LOGIN BUTTON
        // =========================

        loginBtn.setOnClickListener {

            val request =
                LoginRequest(
                    email.text.toString(),
                    password.text.toString()
                )

            api.login(request)
                .enqueue(object : Callback<LoginResponse> {

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {

                        if (response.isSuccessful &&
                            response.body() != null
                        ) {

                            val res = response.body()!!

                            val token =
                                res.token ?: ""

                            val gid =
                                res.user?.gid ?: 0

                            val uid =
                                res.user?.uid ?: 0

                            val username =
                                res.user?.username ?: "User"

                            // =========================
                            // SAVE LOGIN DATA
                            // =========================

                            val pref =
                                getSharedPreferences(
                                    "FashionHub",
                                    MODE_PRIVATE
                                )

                            pref.edit()
                                .putString("token", token)
                                .putInt("gid", gid)
                                .putInt("uid", uid)
                                .putString("name", username)
                                .apply()

                            Toast.makeText(
                                this@LoginActivity,
                                "Login Success",
                                Toast.LENGTH_SHORT
                            ).show()

                            // =========================
                            // REDIRECT
                            // =========================

                            if (gid == 2) {

                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        AdminDashboardActivity::class.java
                                    )
                                )

                            } else {

                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        UserDashboardActivity::class.java
                                    )
                                )
                            }

                            finish()

                        } else {

                            Toast.makeText(
                                this@LoginActivity,
                                "Invalid Login",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<LoginResponse>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@LoginActivity,
                            t.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}