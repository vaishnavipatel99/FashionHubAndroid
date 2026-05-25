package com.example.fashionhubapp.activities.admin

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.R
import com.example.fashionhubapp.adapters.CategoryAdapter
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.Category
import com.example.fashionhubapp.model.CategoryRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardActivity : AppCompatActivity(),
    CategoryAdapter.CategoryActionListener {

    lateinit var recycler: RecyclerView
    lateinit var txtCount: TextView
    lateinit var adapter: CategoryAdapter
    var list = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        recycler = findViewById(R.id.categoryRecycler)
        txtCount = findViewById(R.id.txtCategoryCount)
        val addBtn = findViewById<Button>(R.id.addCategoryBtn)

        recycler.layoutManager = LinearLayoutManager(this)
        adapter = CategoryAdapter(list, this)
        recycler.adapter = adapter

        loadCategories()

        addBtn.setOnClickListener {
            openAddCategoryDialog()
        }
    }

    //  LOAD DATA
    private fun loadCategories() {
        RetrofitClient.instance.getCategories()
            .enqueue(object : Callback<List<Category>> {

                override fun onResponse(
                    call: Call<List<Category>>,
                    response: Response<List<Category>>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        list.clear()
                        list.addAll(response.body()!!)
                        adapter.notifyDataSetChanged()

                        txtCount.text = list.size.toString()

                    } else {
                        Toast.makeText(this@AdminDashboardActivity, "No Data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                    Toast.makeText(this@AdminDashboardActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    //  ADD CATEGORY
    private fun openAddCategoryDialog() {

        val view = layoutInflater.inflate(R.layout.dialog_add_category, null)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        dialog.show()

        val name = view.findViewById<EditText>(R.id.name)
        val occasion = view.findViewById<EditText>(R.id.occasion)
        val season = view.findViewById<EditText>(R.id.season)
        val isActive = view.findViewById<CheckBox>(R.id.isActive)
        val saveBtn = view.findViewById<Button>(R.id.saveBtn)

        saveBtn.setOnClickListener {

            if (name.text.isEmpty()) {
                Toast.makeText(this, "Enter category name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = CategoryRequest(
                cid = 0,
                categoryName = name.text.toString(),
                occasion = occasion.text.toString(),
                season = season.text.toString(),
                isActive = isActive.isChecked
            )

            RetrofitClient.instance.createCategory(request)
                .enqueue(object : Callback<Any> {

                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AdminDashboardActivity, "Category Added", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            loadCategories()
                        } else {
                            Toast.makeText(this@AdminDashboardActivity, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        Toast.makeText(this@AdminDashboardActivity, t.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    //  DELETE CATEGORY
    override fun onDelete(category: Category) {

        AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes") { _, _ ->

                RetrofitClient.instance.deleteCategory(category.cid)
                    .enqueue(object : Callback<Any> {

                        override fun onResponse(call: Call<Any>, response: Response<Any>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@AdminDashboardActivity, "Deleted", Toast.LENGTH_SHORT).show()
                                loadCategories()
                            }
                        }

                        override fun onFailure(call: Call<Any>, t: Throwable) {
                            Toast.makeText(this@AdminDashboardActivity, t.message, Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            .setNegativeButton("No", null)
            .show()
    }

    //  EDIT CATEGORY
    override fun onEdit(category: Category) {

        val view = layoutInflater.inflate(R.layout.dialog_add_category, null)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        dialog.show()

        val name = view.findViewById<EditText>(R.id.name)
        val occasion = view.findViewById<EditText>(R.id.occasion)
        val season = view.findViewById<EditText>(R.id.season)
        val isActive = view.findViewById<CheckBox>(R.id.isActive)
        val saveBtn = view.findViewById<Button>(R.id.saveBtn)

        // Prefill
        name.setText(category.categoryName)
        occasion.setText(category.occasion)
        season.setText(category.season)
        isActive.isChecked = category.isActive

        saveBtn.text = "Update Category"

        saveBtn.setOnClickListener {

            val request = CategoryRequest(
                cid = category.cid,
                categoryName = name.text.toString(),
                occasion = occasion.text.toString(),
                season = season.text.toString(),
                isActive = isActive.isChecked
            )

            RetrofitClient.instance.updateCategory(category.cid, request)
                .enqueue(object : Callback<Any> {

                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AdminDashboardActivity, "Updated", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            loadCategories()
                        }
                    }

                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        Toast.makeText(this@AdminDashboardActivity, t.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}