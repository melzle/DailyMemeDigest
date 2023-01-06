package com.sukacita.dailymemedigest

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // coba coba aja
        val sharedFile = "com.sukacita.dailymemedigest"
        var shared : SharedPreferences = getSharedPreferences(sharedFile, Context.MODE_PRIVATE)
        val user = shared.getString("user", null)
        val obj = JSONObject(user)
        val data = obj.getJSONArray("data")
        val userObj = data.getJSONObject(0)

        val txt: TextView = findViewById(R.id.textView4)
        txt.text = userObj.getString("lastname")

    }
}