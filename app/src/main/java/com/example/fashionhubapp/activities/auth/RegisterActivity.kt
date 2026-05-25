package com.example.fashionhubapp.activities.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionhubapp.R
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText

    private lateinit var registerBtn: Button
    private lateinit var goLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        // =========================
        // FIND VIEWS
        // =========================

        name = findViewById(R.id.name)

        email = findViewById(R.id.email)

        password = findViewById(R.id.password)

        confirmPassword =
            findViewById(R.id.confirmPassword)

        registerBtn =
            findViewById(R.id.registerBtn)

        goLogin =
            findViewById(R.id.goLogin)

        // =========================
        // GO LOGIN
        // =========================

        goLogin.setOnClickListener {

            startActivity(
                Intent(
                    this@RegisterActivity,
                    LoginActivity::class.java
                )
            )

            finish()
        }

        // =========================
        // REGISTER BUTTON
        // =========================

        registerBtn.setOnClickListener {

            val username =
                name.text.toString().trim()

            val userEmail =
                email.text.toString().trim()

            val userPassword =
                password.text.toString().trim()

            val confirmPass =
                confirmPassword.text.toString().trim()

            // =========================
            // VALIDATION
            // =========================

            if (username.isEmpty()) {

                name.error = "Enter Name"

                name.requestFocus()

                return@setOnClickListener
            }

            if (userEmail.isEmpty()) {

                email.error = "Enter Email"

                email.requestFocus()

                return@setOnClickListener
            }

            if (userPassword.isEmpty()) {

                password.error = "Enter Password"

                password.requestFocus()

                return@setOnClickListener
            }

            if (confirmPass.isEmpty()) {

                confirmPassword.error =
                    "Confirm Password"

                confirmPassword.requestFocus()

                return@setOnClickListener
            }

            if (userPassword != confirmPass) {

                confirmPassword.error =
                    "Password does not match"

                confirmPassword.requestFocus()

                return@setOnClickListener
            }

            // =========================
            // USER ROLE
            // =========================

            val gid = 1

            // =========================
            // REQUEST
            // =========================

            val request = RegisterRequest(
                username = username,
                email = userEmail,
                password = userPassword,
                gid = gid,
                gender = "",
                state = "",
                city = ""
            )

            // =========================
            // API CALL
            // =========================

            RetrofitClient.instance
                .register(request)
                .enqueue(object : Callback<Any> {

                    override fun onResponse(
                        call: Call<Any>,
                        response: Response<Any>
                    ) {

                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@RegisterActivity,
                                "Registration Successful",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(
                                Intent(
                                    this@RegisterActivity,
                                    LoginActivity::class.java
                                )
                            )

                            finish()

                        } else {

                            Toast.makeText(
                                this@RegisterActivity,
                                "Registration Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<Any>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@RegisterActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }
}