package com.example.fashionhubapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : BaseActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMobile: EditText
    private lateinit var etCity: EditText
    private lateinit var etState: EditText
    private lateinit var btnUpdate: Button

    private var userId = 0
    private lateinit var currentUser: UserProfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupDrawer("My Profile")

        val pref = getSharedPreferences("FashionHub", MODE_PRIVATE)
        userId = pref.getInt("uid", 0)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etMobile)
        etCity = findViewById(R.id.etCity)
        etState = findViewById(R.id.etState)

        btnUpdate = findViewById(R.id.btnUpdate)

        getUserProfile()

        btnUpdate.setOnClickListener {
            updateProfile()
        }
    }

    private fun getUserProfile() {

        RetrofitClient.instance.getUserById(userId)
            .enqueue(object : Callback<UserProfile> {

                override fun onResponse(
                    call: Call<UserProfile>,
                    response: Response<UserProfile>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        currentUser = response.body()!!

                        etName.setText(currentUser.username)
                        etEmail.setText(currentUser.email)
                        etMobile.setText(currentUser.mobileno)
                        etCity.setText(currentUser.city)
                        etState.setText(currentUser.state)
                    }
                }

                override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun updateProfile() {

        currentUser.username = etName.text.toString()
        currentUser.email = etEmail.text.toString()
        currentUser.mobileno = etMobile.text.toString()
        currentUser.city = etCity.text.toString()
        currentUser.state = etState.text.toString()

        RetrofitClient.instance.updateUser(userId, currentUser)
            .enqueue(object : Callback<Any> {

                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    Toast.makeText(this@ProfileActivity, "Profile Updated", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
    }
}