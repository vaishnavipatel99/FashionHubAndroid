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

    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var loginBtn: Button
    lateinit var goRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        loginBtn = findViewById(R.id.loginBtn)
        goRegister = findViewById(R.id.goRegister)

        val api = RetrofitClient.instance

        // Go to Register Page
        goRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        //  Login Click
        loginBtn.setOnClickListener {

            val request = LoginRequest(
                email.text.toString(),
                password.text.toString()
            )

            api.login(request).enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {

                        val res = response.body()

                        val token = res?.token ?: ""
                        val gid = res?.user?.gid ?: 0

                        //  Save data
                        val pref = getSharedPreferences("fashionhub", MODE_PRIVATE)
                        pref.edit()
                            .putString("token", token)
                            .putInt("gid", gid)
                            .apply()

                        Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()

                        //  Redirect based on gid
                        if (gid == 2) {
                            startActivity(Intent(this@LoginActivity, AdminDashboardActivity::class.java))
                        } else {
                            startActivity(Intent(this@LoginActivity, UserDashboardActivity::class.java))
                        }

                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid Login", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}