package com.example.fashionhubapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val name = findViewById<EditText>(R.id.name)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val registerBtn = findViewById<Button>(R.id.registerBtn)

        registerBtn.setOnClickListener {

            val username = name.text.toString().trim()
            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString().trim()

            //  Validation
            if (username.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Auto role (User)
            val gid = 1

            // Create request (same as React)
            val request = RegisterRequest(
                username = username,
                email = userEmail,
                password = userPassword,
                gid = gid,
                gender = "",
                state = "",
                city = ""
            )

            // API Call
            RetrofitClient.instance.register(request)
                .enqueue(object : Callback<Any> {

                    override fun onResponse(call: Call<Any>, response: Response<Any>) {

                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@RegisterActivity,
                                "Registered Successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            //  Go to login
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()

                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Register Failed: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Any>, t: Throwable) {
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